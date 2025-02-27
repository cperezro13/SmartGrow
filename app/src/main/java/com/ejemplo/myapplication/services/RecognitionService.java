package com.ejemplo.myapplication.services;

import android.content.Context;
import android.graphics.Bitmap;

import com.ejemplo.myapplication.ml.Classifier;
import com.ejemplo.myapplication.utils.ImageUtils;

public class RecognitionService {

    private Classifier classifier;
    private String[] labels = {"Haworthiopsis fasciata", "Aloe rauhii", "Echinocactus platyacanthus",
            "Crassula Pyramidalis", "Mammillaria plumosa", "Aeonium arboreum Zwartkop"};

    public RecognitionService(Context context) throws Exception {
        // Se carga el modelo TFLite
        classifier = new Classifier(context.getAssets(), "model_unquant.tflite");
    }

    // Callback para retornar el resultado del reconocimiento
    public interface RecognitionCallback {
        void onResult(String plantName, float confidence);
    }

    // Realiza el reconocimiento de la planta a partir de un Bitmap
    public void recognizePlant(Bitmap bitmap, RecognitionCallback callback) {
        // Redimensionar la imagen a 224x224
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
        // Preprocesar la imagen usando
        float[][][][] input = ImageUtils.preprocessImage(resizedBitmap);
        // Obtener la predicción del modelo
        float[][] results = classifier.predict(input);

        int bestMatch = -1;
        float bestConfidence = 0;
        for (int i = 0; i < results[0].length; i++) {
            if (results[0][i] > bestConfidence) {
                bestConfidence = results[0][i];
                bestMatch = i;
            }
        }
        String plantName = labels[bestMatch];
        callback.onResult(plantName, bestConfidence);
    }

    public void close() {
        classifier.close();
    }
}
