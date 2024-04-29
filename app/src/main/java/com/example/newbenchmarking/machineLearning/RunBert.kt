package com.example.newbenchmarking.machineLearning

import android.content.Context
import android.util.Log
import com.example.newbenchmarking.interfaces.Inference
import com.example.newbenchmarking.interfaces.InferenceParams
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer
import org.tensorflow.lite.task.text.qa.BertQuestionAnswerer.BertQuestionAnswererOptions
import kotlin.system.measureTimeMillis

fun runBert(androidContext: Context, params: InferenceParams): Inference {

    val baseOptions = BaseOptions.builder()
        .setNumThreads(params.numThreads)

    if(params.useGPU)
        baseOptions.useGpu()
    if(params.useNNAPI)
        baseOptions.useNnapi()

    val options = BertQuestionAnswererOptions.builder()
        .setBaseOptions(baseOptions.build())
        .build()

    val answerer: BertQuestionAnswerer

    val loadTime = measureTimeMillis {
        answerer = BertQuestionAnswerer.createFromFileAndOptions(
            androidContext, params.model.filename, options
        )
    }

    val contextOfTheQuestion = "O Motorola Edge 30 Pro recuperará normalmente as configurações para usar MMS a partir do cartão SIM ou as receberá por rede. As etapas a seguir contêm instruções sobre como configurar manualmente MMS no Motorola Edge 30 Pro. Acesse a tela inicial do aparelho. Para abrir o menu, deslize para cima. Deslize a tela de baixo para cima. Clique em Configurar. Clique em Rede e internet. Clique em Rede móvel. Escolha um cartão SIM, neste caso, SIM 1. Deslize a tela de baixo para cima. Clique em Nomes dos pontos de acesso. Clique no símbolo de mais. No campo Nome, digite Claro MMS. No campo APN, digite mms.claro.com.br. No campo Nome de usuário, digite claro. No campo Senha, digite claro. No campo MMSC, digite http://mms.claro.com.br. No campo Proxy de MMS, digite 200.169.126.010. Deslize a tela de baixo para cima. No campo Porta MMS, digite 8080. No campo MCC, digite 724. No campo MNC, digite 05. No campo Tipo de autenticação, marque None. Clique em Tipo de APN. Se esta tela for exibida, digite mms (obs.: em letras minúsculas) e clique em OK. Se esta tela não for exibida, marque MMS e clique em OK. Clique no símbolo de opções. Clique em Salvar. Volte para a tela inicial do aparelho. Para concluir o processo e ativar as configurações, é preciso reiniciar o dispositivo. Seu Motorola Edge 30 Pro estará, então, configurado para usar MSM. Para obter instruções sobre como reiniciar seu dispositivo, siga estas etapas. Para reiniciar o Motorola Edge 30 Pro, clique e mantenha pressionado o botão de ligar/desligar na lateral do Motorola Edge 30 Pro. Clique em Reiniciar. O Motorola Edge 30 Pro será inicializado automaticamente. Se esta tela for exibida, digite o PIN code do cartão SIM 1 e selecione o símbolo de seta no lado direito. Se esta tela não for exibida, pule para a próxima etapa. Se esta tela for exibida, digite o PIN code do cartão SIM 2 e selecione o símbolo de seta no lado direito. Se esta tela não for exibida, pule para a próxima etapa. Se esta tela for exibida, deslize o dedo por cima da tela para desbloqueá-la. Se esta tela não for exibida, pule para a próxima etapa. O Motorola Edge 30 Pro agora está pronto para uso."
    val questionToAsk = "o que fazer depois de clicar em configurar?"

    val numTests = params.numImages
    var totalTime = 0L
    var firstInferenceTime: Long? = null

    for(i in 1..numTests){
        val inferenceTime = measureTimeMillis {
            val answers = answerer.answer(contextOfTheQuestion, questionToAsk)
            Log.d("Answers", answers[0].text)
        }

        if(i != 1){
            totalTime += inferenceTime
        }else{
            firstInferenceTime = inferenceTime
        }
    }

    return Inference(
        load = loadTime.toInt(),
        average = (totalTime/(numTests-1)).toInt(),
        first = firstInferenceTime?.toInt(),
        standardDeviation = null
    )
}