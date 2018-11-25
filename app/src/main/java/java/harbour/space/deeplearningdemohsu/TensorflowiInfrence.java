package java.harbour.space.deeplearningdemohsu;

import org.tensorflow.lite.Interpreter;
import android.graphics.Bitmap;

import java.io.*;
import android.content.res.AssetManager;
import android.content.res.AssetFileDescriptor;
import java.nio.channels.FileChannel;
import java.nio.*;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.PriorityQueue;

public class TensorflowiInfrence implements Classifier
{
    private static final int MAX_RESULT=1;
    private static final int BATCH_SIZE=1;
    private static final int IMAGE_SIZE=5;
    private static final float TOLERENCE= 0.1f;
    private Interpreter tfInterpreter;
    private List<String> labels;
private int inputSize;
    private TensorflowiInfrence()
    {

    }
    static Classifier create(AssetManager assetManager,
                             String modelPath,
                             String labelPath,
                             int inputSize) throws IOException {

        TensorflowiInfrence classifier = new TensorflowiInfrence();
        classifier.tfInterpreter = new Interpreter(classifier.loadModel(assetManager, modelPath));
        classifier.labels = classifier.loadLabels(assetManager, labelPath);
        classifier.inputSize = inputSize;

        return classifier;
    }
    @Override
    public List<Classification> recognizeImage(Bitmap bitmap)
    {
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);
        byte[][] result = new byte[1][labels.size()];
        tfInterpreter.run(byteBuffer, result);
        return getSortedResult(result);
    }

    @Override
    public void close()
    {
        tfInterpreter.close();
        tfInterpreter = null;

    }

    private MappedByteBuffer loadModel(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * inputSize * inputSize * IMAGE_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.put((byte) ((val >> 16) & 0xFF));
                byteBuffer.put((byte) ((val >> 8) & 0xFF));
                byteBuffer.put((byte) (val & 0xFF));
            }
        }
        return byteBuffer;
    }

    private List<Classification> getSortedResult(byte[][] accarray)
    {
        PriorityQueue<Classification> pq =
                new PriorityQueue<>(
                        MAX_RESULT,
                        new Comparator<Classification>() {
                            @Override
                            public int compare(Classification o1, Classification o2) {
                                return Float.compare(o1.getOutputProbability(), o2.getOutputProbability());
                            }
                        });
        for (int i = 0; i < labels.size(); ++i)
        {
            float confidence = (accarray[0][i] & 0xff) / 255.0f;
            if (confidence > TOLERENCE) {
                pq.add(new Classification("" + i,
                        labels.size() > i ? labels.get(i) : "unknown",
                        confidence));
            }
        }

        final ArrayList<Classification> classes = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULT);
        for (int i = 0; i < recognitionsSize; ++i)
        {
            classes.add(pq.poll());
        }

        return classes;

    }
}
