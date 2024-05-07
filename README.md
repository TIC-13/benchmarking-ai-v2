# Speed.AI - AI Benchmarking on Android

## About the App

Speed.AI is an Android application designed for benchmarking TensorFlow Lite AI models.

## Showcase

<div style="display: flex; flex-direction: 'row'">
  <img src="https://github.com/TIC-13/benchmarking-ai-v2/assets/62716614/9b784c83-7f2c-46c4-8cd7-38a2944521d5" alt="tela inicial" style="width: 30%;" />
  <img src="https://github.com/TIC-13/benchmarking-ai-v2/assets/62716614/bdbb8c8d-4dda-4140-8b1b-bee14199931a" alt=resultado" style="width: 30%; margin-right: 4%;" />
  <img src="https://github.com/TIC-13/benchmarking-ai-v2/assets/62716614/f52e5186-ec75-423a-a623-b326528ad768" alt=carregando" style="width: 30%; margin-right: 4%;" />
</div>  

### Features

- **Default Tests**:
  - Run a pre-configured benchmarking test using over 10 different models.

- **Custom Tests**:
  - Create your own tests with parameters such as:
    - Number of threads
    - Number of images
    - Acceleration using the GPU delegate or NNAPI.

- **Benchmarking Tests**
  - Vision tasks:
    - Classification
    - Segmentation
    - Detection
    - Image Super Resolution
    - Image Deblurring
  - Support for the BERT language model.

### Measured Metrics

Speed.AI measures several performance metrics, including:

- Initialization speed
- First inference speed
- Average inference speed (excluding the first inference)
- Standard deviation of the inference speed
- Average and peak usage values for:
  - CPU
  - GPU
  - RAM

## How to Add New Models

To add new models in Speed.AI, follow these steps:

1. **Place the Model File**:
   - Place the `.tflite` file in the `ml` folder.

2. **Modify the models.yaml file**:
   - Open the file `models.yaml`, inside the `assets` folder.
   - Add a model to the models list, like the example bellow.

<br/>
  
   ```yaml
  - name: "Efficientnet"
    type: "Classificação de imagem"
    description: "O EfficientNet é um modelo de machine learning otimizado para classificação de imagens. Sua arquitetura eficiente e escalável o torna versátil para lidar com uma variedade de desafios em visão computacional."
    file: "efficientNetFP32.tflite"
    category: "CLASSIFICATION"
    quantization: "FP32"
```
<br/>

3. **About the model structure**:
   - The available categories and quantizations are defined in the Models.kt file, inside the interfaces folder:
   - You can also define the shape of the models inputs and outputs in the `models.yaml` file. In that case, they will override the values that are retrieved automatically from the model. See the example bellow:

  <br/>
  
   ```yaml
  - name: "Yolo v4"
    type: "Detecção de objeto"
    description: "hello."
    file: "--yolov4-tiny-416-fp32.tflite"
    inputShape: [1, 416, 416, 3]
    outputShape: [1, 416, 416, 3]
    category: "DETECTION"
    quantization: "FP32"
```
     

