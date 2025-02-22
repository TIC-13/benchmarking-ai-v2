# Speed.AI - AI Benchmarking on Android

## About the App

Speed.AI is an Android application designed for benchmarking TensorFlow Lite AI models.

## Showcase

<div style="display: flex; flex-direction: 'row'">
  <img src="https://github.com/user-attachments/assets/83ff3c59-5c76-4556-8b5a-1394aba105d7" alt="tela inicial" style="width: 30%;" />
  <img src="https://github.com/user-attachments/assets/757a4c5a-e54e-46d2-a2e5-623a91b70ea8" alt=executando" style="width: 30%; margin-right: 4%;" />
  <img src="https://github.com/user-attachments/assets/fafee564-7153-42c8-a1ac-1b8673dacd7b" alt=carregando" style="width: 30%; margin-right: 4%;" />
</div>  

### Features

- **Default Tests**:
  - Run a pre-configured benchmarking test using over 10 different models.

- **Custom Tests**:
  - Create your own tests with parameters such as:
    - Number of threads
    - Number of images
    - Acceleration using the GPU delegate ~~or NNAPI~~ (NNAPI usage is [discontinued](https://web.archive.org/web/20250117001113/https://developer.android.com/ndk/guides/neuralnetworks)).

- **Benchmarking Tests**
  - Vision tasks:
    - Classification
    - Segmentation
    - Detection
    - Image Super Resolution
    - Image Deblurring

### Measured Metrics

Speed.AI measures several performance metrics, including:

- Initialization speed
- First inference speed
- Average inference speed (Using Trapezoidal rule and excluding the first inference)
- Standard deviation of the inference speed
- Average and peak usage values for:
  - CPU (Only in the debug version)
  - GPU
  - RAM
 
## About the ranking

This app sends benchmarking results to a ranking system, where you can view the benchmarking results by each phone that has run it.

The ranking is shared with the [Speed.AI - LLM Version](https://github.com/TIC-13/llm-benchmark-mobile/tree/dev) app.
If you want to host an instance of the ranking, see these repositories: [Front](https://github.com/TIC-13/benchmark-ranking-front) | [Back](https://github.com/TIC-13/benchmark-ranking-back).

Not hosting the ranking has no impact on the app’s functionality—you can still build and use the app normally.

## How to add new models without changing the source code

To add new models in Speed.AI, follow these steps:

1. **Go to `Start Custom Test`**:  
   - From the main screen, press the `Start Custom Test` button.

2. **Allow the app access to the phone's storage**:  
   - Click the `Allow Access` button and select the Speed.AI app.

3. **Add the `.tflite` model to the Speed.AI folder**:  
   - The app will create a Speed.AI folder in the root of the phone's file system.  
   - Move the `.tflite` file to this folder.  
   - Once the file is in place, you can use the model in the Custom Test section (accessible after pressing the `Start Custom Test` button on the main screen).

**Warning**: For the model to run properly, it must have correct metadata, especially the input size.

<br/>

## How to add new models to the benchmarking by changing the source code

To add new models in Speed.AI, follow these steps:

1. **Place the Model File**:
   - Place the `.tflite` file in the `ml` folder.

2. **Modify the models.yaml file**:
   - Open the file `models.yaml`, inside the `assets` folder.
   - Add a model to the models list, like the example bellow.

<br/>
  
   ```yaml
  - id: 1
    name: "Efficientnet"
    type: "Classificação de imagem"
    description: "O EfficientNet é um modelo de machine learning otimizado para classificação de imagens. Sua arquitetura eficiente e escalável o torna versátil para lidar com uma variedade de desafios em visão computacional."
    file: "efficientNetFP32.tflite"
    category: "CLASSIFICATION"
    quantization: "FP32"
```
<br/>

3. **About the model structure**:
   - The available categories and quantizations are defined in the Models.kt file, inside the interfaces folder:
   - You can also define the shape of the models inputs and outputs in the `models.yaml` file. In that case, they will override the values that are retrieved from the model's metadata. See the example bellow:

  <br/>
  
   ```yaml
  - id: 3
    name: "Yolo v4"
    type: "Detecção de objeto"
    description: "hello."
    file: "--yolov4-tiny-416-fp32.tflite"
    inputShape: [1, 416, 416, 3]
    outputShape: [1, 416, 416, 3]
    category: "DETECTION"
    quantization: "FP32"
```

4. **Add to the benchmarking**
   - If you want to add or remove model executions to the benchmarking, change the file `tests.yaml`. See bellow the example of a declaration of a benchmarking in the file:

  ```yaml
  - model_id: 1
  dataset_id: 1
  runMode: "CPU"
  numThreads: 1
  numSamples: 200
  ```

4. **Add datasets**
   - If you want to add a dataset to the app, add a folder with the images to the `assets` folder, and add a declaration like the one bellow in the `datasets.yaml` file:

  ```yaml
  - id: 1
    name: "Imagenet"
    path: "images_dataset" (dataset folder name)
    size: 400
  ```   

## Environment  

In the Android project, if you are hosting the ranking, set the backend address in `local.properties` under `API_ADDRESS`. Assign a base64-encoded 32-byte (AES-256) value to `API_KEY`, ensuring it matches the key used in the backend.  

Additionally, in `local.properties`, set `RANKING_ADDRESS` to the address where the ranking is hosted.  




