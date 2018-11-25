package java.harbour.space.deeplearningdemohsu;
import java.util.List;
import android.graphics.Bitmap;
public interface Classifier
{
    class Classification
    {
        private final String classId;
        private final String idOuput;
        private final float outputProbability;

        public String getClassId() {
            return classId;
        }

        public String getIdOuput() {
            return idOuput;
        }

        public float getOutputProbability() {
            return outputProbability;
        }

        public Classification(String classId, String idOuput, float outputProbability)
        {
            this.classId = classId;
            this.idOuput = idOuput;
            this.outputProbability = outputProbability;
        }

        @Override
        public String toString()
        {
            String resultString = "";
            if (classId != null) {
                resultString += "[" + classId + "] ";
            }

            if (idOuput != null) {
                resultString += idOuput + " ";
            }

            if (outputProbability != 0) {
                resultString += String.format("(%.1f%%) ", outputProbability * 100.0f);
            }

            return resultString.trim();
        }
    }


    List<Classification> recognizeImage(Bitmap bitmap);

    void close();


}
