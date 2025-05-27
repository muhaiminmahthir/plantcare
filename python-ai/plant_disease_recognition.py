import torch
import torch.nn as nn
import torch.nn.functional as F
import torchvision.transforms as transforms
from PIL import Image
import matplotlib.pyplot as plt
import numpy as np
import os
import argparse
import json
import sys

# Device configuration
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# ResNet9 Model Architecture (same as training)
def ConvBlock(in_channels, out_channels, pool=False):
    layers = [nn.Conv2d(in_channels, out_channels, kernel_size=3, padding=1),
             nn.BatchNorm2d(out_channels),
             nn.ReLU(inplace=True)]
    if pool:
        layers.append(nn.MaxPool2d(4))
    return nn.Sequential(*layers)

class ResNet9(nn.Module):
    def __init__(self, in_channels, num_diseases):
        super().__init__()
        
        self.conv1 = ConvBlock(in_channels, 64)
        self.conv2 = ConvBlock(64, 128, pool=True)
        self.res1 = nn.Sequential(ConvBlock(128, 128), ConvBlock(128, 128))
        
        self.conv3 = ConvBlock(128, 256, pool=True)
        self.conv4 = ConvBlock(256, 512, pool=True)
        self.res2 = nn.Sequential(ConvBlock(512, 512), ConvBlock(512, 512))
        
        self.classifier = nn.Sequential(nn.MaxPool2d(4),
                                       nn.Flatten(),
                                       nn.Linear(512, num_diseases))
        
    def forward(self, xb):
        out = self.conv1(xb)
        out = self.conv2(out)
        out = self.res1(out) + out
        out = self.conv3(out)
        out = self.conv4(out)
        out = self.res2(out) + out
        out = self.classifier(out)
        return out

# Plant disease class names
class_names = [
    'Apple___Apple_scab', 'Apple___Black_rot', 'Apple___Cedar_apple_rust', 'Apple___healthy',
    'Blueberry___healthy', 'Cherry_(including_sour)___Powdery_mildew', 
    'Cherry_(including_sour)___healthy', 'Corn_(maize)___Cercospora_leaf_spot Gray_leaf_spot', 
    'Corn_(maize)___Common_rust_', 'Corn_(maize)___Northern_Leaf_Blight', 'Corn_(maize)___healthy', 
    'Grape___Black_rot', 'Grape___Esca_(Black_Measles)', 'Grape___Leaf_blight_(Isariopsis_Leaf_Spot)', 
    'Grape___healthy', 'Orange___Haunglongbing_(Citrus_greening)', 'Peach___Bacterial_spot',
    'Peach___healthy', 'Pepper,_bell___Bacterial_spot', 'Pepper,_bell___healthy', 
    'Potato___Early_blight', 'Potato___Late_blight', 'Potato___healthy', 
    'Raspberry___healthy', 'Soybean___healthy', 'Squash___Powdery_mildew', 
    'Strawberry___Leaf_scorch', 'Strawberry___healthy', 'Tomato___Bacterial_spot', 
    'Tomato___Early_blight', 'Tomato___Late_blight', 'Tomato___Leaf_Mold', 
    'Tomato___Septoria_leaf_spot', 'Tomato___Spider_mites Two-spotted_spider_mite', 
    'Tomato___Target_Spot', 'Tomato___Tomato_Yellow_Leaf_Curl_Virus', 
    'Tomato___Tomato_mosaic_virus', 'Tomato___healthy'
]

def load_model(model_path, num_classes=38):
    """Load the trained ResNet9 model"""
    model = ResNet9(3, num_classes)
    model.load_state_dict(torch.load(model_path, map_location=device))
    model.to(device)
    model.eval()
    return model

def get_tta_transforms():
    """Get Test-Time Augmentation transforms"""
    return [
        # Original transform
        transforms.Compose([
            transforms.Resize((256, 256)),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
        ]),
        # Horizontal flip
        transforms.Compose([
            transforms.Resize((256, 256)),
            transforms.CenterCrop(224),
            transforms.RandomHorizontalFlip(p=1.0),
            transforms.ToTensor(),
        ]),
        # Different scale 1
        transforms.Compose([
            transforms.Resize((280, 280)),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
        ]),
        # Different scale 2
        transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
        ]),
        # Slight rotation
        transforms.Compose([
            transforms.Resize((256, 256)),
            transforms.RandomRotation(degrees=10),
            transforms.CenterCrop(224),
            transforms.ToTensor(),
        ]),
        # Color jitter
        transforms.Compose([
            transforms.Resize((256, 256)),
            transforms.CenterCrop(224),
            transforms.ColorJitter(brightness=0.1, contrast=0.1, saturation=0.1),
            transforms.ToTensor(),
        ])
    ]

def enhanced_predict_image(model, image_path, class_names, confidence_threshold=60.0, use_tta=True, num_tta=5):
    """Enhanced prediction with Test-Time Augmentation and confidence checking"""
    
    # Load image
    image = Image.open(image_path).convert('RGB')
    
    if use_tta:
        # Use Test-Time Augmentation
        transforms_list = get_tta_transforms()[:num_tta]
        all_predictions = []
        
        with torch.no_grad():
            for transform in transforms_list:
                try:
                    image_tensor = transform(image).unsqueeze(0).to(device)
                    outputs = model(image_tensor)
                    probabilities = F.softmax(outputs, dim=1)
                    all_predictions.append(probabilities)
                except Exception as e:
                    # Skip failed transformations
                    continue
        
        if not all_predictions:
            # Fallback to simple prediction if all TTA failed
            return simple_predict_image(model, image_path, class_names)
        
        # Average all predictions
        avg_predictions = torch.mean(torch.stack(all_predictions), dim=0)
        confidence, predicted = torch.max(avg_predictions, 1)
        
    else:
        # Simple prediction without TTA
        return simple_predict_image(model, image_path, class_names)
    
    predicted_class = class_names[predicted.item()]
    confidence_score = confidence.item() * 100
    
    # Check confidence level
    is_confident = confidence_score >= confidence_threshold
    confidence_level = get_confidence_level(confidence_score)
    
    return image, predicted_class, confidence_score, avg_predictions, is_confident, confidence_level

def simple_predict_image(model, image_path, class_names):
    """Simple prediction without TTA (fallback)"""
    transform = transforms.Compose([
        transforms.Resize((256, 256)),
        transforms.ToTensor(),
    ])
    
    image = Image.open(image_path).convert('RGB')
    image_tensor = transform(image).unsqueeze(0).to(device)
    
    with torch.no_grad():
        outputs = model(image_tensor)
        probabilities = F.softmax(outputs, dim=1)
        confidence, predicted = torch.max(probabilities, 1)
    
    predicted_class = class_names[predicted.item()]
    confidence_score = confidence.item() * 100
    confidence_level = get_confidence_level(confidence_score)
    
    return image, predicted_class, confidence_score, probabilities, confidence_score >= 60.0, confidence_level

def get_confidence_level(confidence_score):
    """Categorize confidence level"""
    if confidence_score >= 85:
        return "Very High"
    elif confidence_score >= 70:
        return "High"
    elif confidence_score >= 55:
        return "Medium"
    elif confidence_score >= 40:
        return "Low"
    else:
        return "Very Low"

def generate_enhanced_recommendation(plant_name, disease_name, is_healthy, confidence_score, confidence_level):
    """Generate enhanced recommendations based on confidence and disease type"""
    
    base_recommendation = ""
    
    if is_healthy:
        base_recommendation = "Plant appears healthy. Continue regular care routine."
    else:
        # Disease-specific recommendations
        disease_recommendations = {
            "bacterial spot": "Remove affected leaves, improve air circulation, avoid overhead watering, consider copper-based fungicide.",
            "leaf scorch": "Ensure adequate watering, provide shade during hot weather, check for root problems.",
            "early blight": "Remove affected foliage, improve air circulation, apply fungicide if severe.",
            "late blight": "Remove affected parts immediately, avoid overhead watering, apply preventive fungicide.",
            "apple scab": "Remove fallen leaves, prune for air circulation, apply fungicide in early spring.",
            "black rot": "Remove infected fruits and branches, improve air circulation, apply fungicide.",
            "powdery mildew": "Improve air circulation, reduce humidity, apply sulfur-based fungicide.",
            "common rust": "Ensure good drainage, avoid overcrowding, apply fungicide if needed.",
            "leaf blight": "Remove affected leaves, improve air circulation, avoid overhead watering."
        }
        
        disease_lower = disease_name.lower()
        specific_recommendation = None
        
        for key, rec in disease_recommendations.items():
            if key in disease_lower:
                specific_recommendation = rec
                break
        
        if specific_recommendation:
            base_recommendation = f"Plant shows signs of {disease_name}. {specific_recommendation}"
        else:
            base_recommendation = f"Plant shows signs of {disease_name}. Remove affected areas, improve growing conditions, and monitor closely."
    
    # Add confidence-based warnings
    if confidence_level in ["Low", "Very Low"]:
        base_recommendation += f" ⚠️ Note: Confidence is {confidence_level.lower()} ({confidence_score:.1f}%) - consider retaking photo with better lighting and focus."
    elif confidence_level == "Medium":
        base_recommendation += f" ℹ️ Confidence: {confidence_level} ({confidence_score:.1f}%) - recommendation is reasonably reliable."
    
    return base_recommendation

# Main execution
if __name__ == "__main__":
    # Parse command line arguments
    parser = argparse.ArgumentParser()
    parser.add_argument('--image', required=True, help='Path to input image')
    parser.add_argument('--output', default='json', help='Output format')
    parser.add_argument('--tta', action='store_true', default=True, help='Use Test-Time Augmentation')
    parser.add_argument('--confidence-threshold', type=float, default=60.0, help='Confidence threshold')
    args = parser.parse_args()
    
    # Get the directory where the script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    model_path = os.path.join(script_dir, "plant-disease-model.pth")
    
    try:
        model = load_model(model_path, num_classes=len(class_names))
    except Exception as e:
        error_result = {
            "error": f"Model loading failed: {str(e)}",
            "success": False
        }
        print(json.dumps(error_result))
        sys.exit(1)
    
    # Analyze the provided image with enhanced prediction
    try:
        image, predicted_class, confidence_score, probabilities, is_confident, confidence_level = enhanced_predict_image(
            model, args.image, class_names, 
            confidence_threshold=args.confidence_threshold,
            use_tta=args.tta,
            num_tta=5
        )
        
        # Parse results
        plant_name = predicted_class.split('___')[0].replace('_', ' ')
        disease_name = predicted_class.split('___')[1].replace('_', ' ')
        is_healthy = disease_name.lower() == 'healthy'
        
        # Get top 5 predictions for better insight
        top_probs, top_indices = torch.topk(probabilities[0], 5)
        top_predictions = []
        for i in range(5):
            class_name = class_names[top_indices[i]]
            prob = top_probs[i].item() * 100
            plant_part = class_name.split('___')[0].replace('_', ' ')
            disease_part = class_name.split('___')[1].replace('_', ' ')
            top_predictions.append({
                "className": class_name,
                "plantType": plant_part,
                "disease": disease_part,
                "probability": prob
            })
        
        # Generate enhanced recommendation
        recommendation = generate_enhanced_recommendation(
            plant_name, disease_name, is_healthy, confidence_score, confidence_level
        )
        
        # Check for conflicting predictions (potential misclassification)
        second_best_prob = top_predictions[1]["probability"] if len(top_predictions) > 1 else 0
        is_uncertain = (confidence_score - second_best_prob) < 20  # Close predictions
        
        # Create enhanced result JSON
        result = {
            "success": True,
            "plantType": plant_name,
            "diseaseStatus": "Healthy" if is_healthy else "Diseased",
            "diseaseName": disease_name,
            "confidence": confidence_score,
            "confidenceLevel": confidence_level,
            "isHealthy": is_healthy,
            "isConfident": is_confident,
            "isUncertain": is_uncertain,
            "recommendation": recommendation,
            "topPredictions": top_predictions,
            "analysisMethod": "TTA" if args.tta else "Standard",
            "warning": "" if is_confident else f"Low confidence prediction - consider retaking photo"
        }
        
        print(json.dumps(result))
        
    except Exception as e:
        error_result = {
            "error": f"Analysis failed: {str(e)}",
            "success": False
        }
        print(json.dumps(error_result))
        sys.exit(1)

#PlantVillage-Dataset/raw/color/Apple___Apple_scab/0a5e9323-dbad-432d-ac58-d291718345d9___FREC_Scab 3417.JPG