package com.example.prototiposemana7;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PersistableBundle;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor rotationSensor;
    private TextView sensorDataTextView;
    private ImageView mImageView;
    private Button downloadButton;
    private Bitmap currentImage; // Variable para almacenar la imagen actual

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        sensorDataTextView = findViewById(R.id.sensorDataTextView);
        mImageView = findViewById(R.id.mImageView);
        downloadButton = findViewById(R.id.downloadButton);

        // Inicializar el sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Registrar el listener del sensor
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                sensorDataTextView.setText("Gatito insano.");
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Manejo de cambios de precisión
            }
        }, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Restaurar la imagen si fue guardada antes
        if (savedInstanceState != null) {
            currentImage = savedInstanceState.getParcelable("imageBitmap");
            if (currentImage != null) {
                mImageView.setImageBitmap(currentImage);
            }
        }

        // Configurar el botón para descargar la imagen
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final Bitmap bitmap = loadImageFromURL("https://i.pinimg.com/736x/97/16/9a/97169a26d5140edf9f8af77d7258b75b.jpg");
                        if (bitmap != null) {
                            currentImage = bitmap;  // Guardar la imagen descargada
                            mImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    mImageView.setImageBitmap(bitmap);
                                }
                            });
                        } else {
                            // Si la descarga falla, mostrar un mensaje
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Error al descargar la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });
    }

    // Método para descargar la imagen desde una URL
    private Bitmap loadImageFromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Retorna null si ocurre un error
        }
    }

    // Guardar el estado de la actividad (imagen descargada)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentImage != null) {
            outState.putParcelable("imageBitmap", currentImage);
        }
    }
}
