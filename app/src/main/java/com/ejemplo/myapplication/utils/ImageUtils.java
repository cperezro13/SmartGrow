package com.ejemplo.myapplication.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import androidx.core.content.FileProvider;
import android.os.Environment;
import java.io.File;

public class ImageUtils {

    public static float[][][][] preprocessImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float[][][][] input = new float[1][height][width][3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                // Normaliza los valores de los pixeles (0-255) a (0-1)
                input[0][y][x][0] = ((pixel >> 16) & 0xFF) / 255.0f; // Canal Rojo
                input[0][y][x][1] = ((pixel >> 8) & 0xFF) / 255.0f;  // Canal Verde
                input[0][y][x][2] = (pixel & 0xFF) / 255.0f;         // Canal Azul
            }
        }

        return input;
    }

    public static String uriToBase64(ContentResolver resolver, Uri uri) {
        try {
            InputStream inputStream = resolver.openInputStream(uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            if (inputStream != null) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            }

            byte[] imageBytes = outputStream.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri createImageFileUri(Context context) {
        try {
            String fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(storageDir, fileName);
            // Asegúrate de que el FileProvider esté configurado correctamente en tu AndroidManifest.xml
            return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", imageFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
