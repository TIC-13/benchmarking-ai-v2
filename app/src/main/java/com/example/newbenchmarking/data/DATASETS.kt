package com.example.newbenchmarking.data

import com.example.newbenchmarking.data.imagesId.IMAGENET_ANIMALS
import com.example.newbenchmarking.interfaces.Dataset

var DATASETS = listOf(
    Dataset(
        label = "Imagenet - Animais",
        imagesId = IMAGENET_ANIMALS
    ),
    Dataset(
        label = "Imagenet - Small",
        imagesId = IMAGENET_ANIMALS.subList(0, 20)
    )
)