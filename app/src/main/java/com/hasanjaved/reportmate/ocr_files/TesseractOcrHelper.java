//package com.hasanjaved.reportmate.ocr_files;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.ColorMatrix;
//import android.graphics.ColorMatrixColorFilter;
//import android.graphics.Paint;
//import android.os.AsyncTask;
//
//import com.googlecode.tesseract.android.TessBaseAPI;
//import com.hasanjaved.reportmate.utility.Utility;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Pattern;
//
//public class TesseractOcrHelper {
//
//    private Context context;
//    private TessBaseAPI tessBaseApi;
//    private boolean isTesseractInitialized = false;
//    private OcrCallback callback;
//
//    // OCR Results
//    public static class OcrResult {
//        public String temperature = "";
//        public String humidity = "";
//        public String rawText = "";
//        public boolean success = false;
//        public String error = "";
//    }
//
//    public interface OcrCallback {
//        void onInitializationComplete(boolean success);
//        void onOcrComplete(OcrResult result);
//    }
//
//    public TesseractOcrHelper(Context context) {
//        this.context = context;
//    }
//
//    public void setCallback(OcrCallback callback) {
//        this.callback = callback;
//    }
//
//    public boolean isInitialized() {
//        return isTesseractInitialized;
//    }
//
//    // TESSERACT INITIALIZATION
//    public void initialize() {
//        new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                try {
//                    // Get the tessdata directory
//                    File dataDir = new File(context.getFilesDir(), "tessdata");
//                    if (!dataDir.exists()) {
//                        dataDir.mkdirs();
//                    }
//
//                    // Copy English training data if it doesn't exist
//                    File engTrainedData = new File(dataDir, "eng.traineddata");
//                    if (!engTrainedData.exists()) {
//                        copyTessDataFile("eng.traineddata", engTrainedData);
//                    }
//
//                    // Initialize Tesseract
//                    tessBaseApi = new TessBaseAPI();
//                    boolean initialized = tessBaseApi.init(context.getFilesDir().getPath(), "eng");
//
//                    if (initialized) {
//                        // Configure Tesseract specifically for LCD/digital displays
//                        tessBaseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK);
//
//                        // Set variables for better digital display recognition
//                        tessBaseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "0123456789.°%CF ");
//                        tessBaseApi.setVariable("tessedit_char_blacklist", "");
//                        tessBaseApi.setVariable("classify_bln_numeric_mode", "1");
//                        tessBaseApi.setVariable("tessedit_ocr_engine_mode", "1"); // Use LSTM OCR Engine
//                        tessBaseApi.setVariable("load_system_dawg", "0");
//                        tessBaseApi.setVariable("load_freq_dawg", "0");
//                        tessBaseApi.setVariable("load_unambig_dawg", "0");
//                        tessBaseApi.setVariable("load_punc_dawg", "0");
//                        tessBaseApi.setVariable("load_number_dawg", "0");
//                        tessBaseApi.setVariable("load_bigram_dawg", "0");
//
//                        Utility.showLog("Tesseract initialized successfully");
//                        return true;
//                    } else {
//                        Utility.showLog("Failed to initialize Tesseract");
//                        return false;
//                    }
//                } catch (Exception e) {
//                    Utility.showLog("Error initializing Tesseract: " + e.getMessage());
//                    return false;
//                }
//            }
//
//            @Override
//            protected void onPostExecute(Boolean success) {
//                isTesseractInitialized = success;
//                if (callback != null) {
//                    callback.onInitializationComplete(success);
//                }
//            }
//        }.execute();
//    }
//
//    private void copyTessDataFile(String filename, File destinationFile) throws IOException {
//        try (InputStream inputStream = context.getAssets().open(filename);
//             FileOutputStream outputStream = new FileOutputStream(destinationFile)) {
//
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//            Utility.showLog("Copied " + filename + " to " + destinationFile.getPath());
//        }
//    }
//
//    // MAIN OCR METHOD
//    public void performOCR(String imagePath) {
//        if (!isTesseractInitialized) {
//            OcrResult result = new OcrResult();
//            result.error = "Tesseract not initialized";
//            if (callback != null) {
//                callback.onOcrComplete(result);
//            }
//            return;
//        }
//
//        new AsyncTask<String, Void, OcrResult>() {
//            @Override
//            protected OcrResult doInBackground(String... paths) {
//                OcrResult result = new OcrResult();
//
//                try {
//                    String imagePath = paths[0];
//
//                    // Load and preprocess the image
//                    Bitmap originalBitmap = BitmapFactory.decodeFile(imagePath);
//                    if (originalBitmap == null) {
//                        result.error = "Failed to load image";
//                        return result;
//                    }
//
//                    // Preprocess image for better OCR
//                    Bitmap processedBitmap = preprocessImageForOCR(originalBitmap);
//
//                    // Set bitmap for Tesseract
//                    tessBaseApi.setImage(processedBitmap);
//
//                    // Get OCR result
//                    String ocrText = tessBaseApi.getUTF8Text();
//
//                    // Clean up
//                    if (processedBitmap != originalBitmap) {
//                        processedBitmap.recycle();
//                    }
//                    originalBitmap.recycle();
//
//                    if (ocrText != null && !ocrText.trim().isEmpty()) {
//                        result.rawText = ocrText.trim();
//                        result.success = true;
//
//                        // Extract temperature and humidity
//                        extractTemperatureAndHumidity(result);
//
//                        Utility.showLog("Tesseract OCR Raw Text: " + result.rawText);
//                        Utility.showLog("Extracted Temperature: " + result.temperature);
//                        Utility.showLog("Extracted Humidity: " + result.humidity);
//                    } else {
//                        result.error = "No text detected";
//                    }
//
//                } catch (Exception e) {
//                    result.error = "OCR processing failed: " + e.getMessage();
//                    Utility.showLog("Error in OCR processing: " + e.getMessage());
//                }
//
//                return result;
//            }
//
//            @Override
//            protected void onPostExecute(OcrResult result) {
//                if (callback != null) {
//                    callback.onOcrComplete(result);
//                }
//            }
//        }.execute(imagePath);
//    }
//
//    // IMAGE PREPROCESSING FOR BETTER OCR
//    private Bitmap preprocessImageForOCR(Bitmap original) {
//        try {
//            // Scale up the image significantly for LCD displays (3x scaling)
//            int scaledWidth = original.getWidth() * 3;
//            int scaledHeight = original.getHeight() * 3;
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true);
//
//            // Convert to grayscale first
//            Bitmap grayscaleBitmap = convertToGrayscale(scaledBitmap);
//
//            // Apply aggressive contrast enhancement for LCD displays
//            Bitmap contrastBitmap = enhanceContrastForLCD(grayscaleBitmap);
//
//            // Apply threshold to create binary image (black/white only)
//            Bitmap binaryBitmap = applyThreshold(contrastBitmap);
//
//            // Apply morphological operations to clean up
//            Bitmap cleanedBitmap = morphologicalClean(binaryBitmap);
//
//            // Clean up intermediate bitmaps
//            if (scaledBitmap != original) scaledBitmap.recycle();
//            if (grayscaleBitmap != scaledBitmap) grayscaleBitmap.recycle();
//            if (contrastBitmap != grayscaleBitmap) contrastBitmap.recycle();
//            if (binaryBitmap != contrastBitmap) binaryBitmap.recycle();
//
//            return cleanedBitmap;
//
//        } catch (Exception e) {
//            Utility.showLog("Error preprocessing image: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap convertToGrayscale(Bitmap original) {
//        try {
//            int width = original.getWidth();
//            int height = original.getHeight();
//            Bitmap grayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//
//            Canvas canvas = new Canvas(grayscale);
//            ColorMatrix colorMatrix = new ColorMatrix();
//            colorMatrix.setSaturation(0);
//
//            Paint paint = new Paint();
//            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//            canvas.drawBitmap(original, 0, 0, paint);
//
//            return grayscale;
//        } catch (Exception e) {
//            Utility.showLog("Error converting to grayscale: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap enhanceContrastForLCD(Bitmap original) {
//        try {
//            int width = original.getWidth();
//            int height = original.getHeight();
//            int[] pixels = new int[width * height];
//            original.getPixels(pixels, 0, width, 0, 0, width, height);
//
//            // Apply aggressive contrast enhancement
//            for (int i = 0; i < pixels.length; i++) {
//                int gray = Color.red(pixels[i]); // Already grayscale
//
//                // Aggressive contrast curve for LCD displays
//                int enhanced = (int) (255 * Math.pow(gray / 255.0, 0.4));
//                enhanced = Math.max(0, Math.min(255, enhanced));
//
//                pixels[i] = Color.rgb(enhanced, enhanced, enhanced);
//            }
//
//            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            result.setPixels(pixels, 0, width, 0, 0, width, height);
//            return result;
//        } catch (Exception e) {
//            Utility.showLog("Error enhancing contrast: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap applyThreshold(Bitmap original) {
//        try {
//            int width = original.getWidth();
//            int height = original.getHeight();
//            int[] pixels = new int[width * height];
//            original.getPixels(pixels, 0, width, 0, 0, width, height);
//
//            // Calculate optimal threshold using Otsu's method (simplified)
//            int[] histogram = new int[256];
//            for (int pixel : pixels) {
//                int gray = Color.red(pixel);
//                histogram[gray]++;
//            }
//
//            int threshold = findOtsuThreshold(histogram, pixels.length);
//
//            // Apply threshold
//            for (int i = 0; i < pixels.length; i++) {
//                int gray = Color.red(pixels[i]);
//                int binary = gray > threshold ? 255 : 0;
//                pixels[i] = Color.rgb(binary, binary, binary);
//            }
//
//            Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            result.setPixels(pixels, 0, width, 0, 0, width, height);
//            return result;
//        } catch (Exception e) {
//            Utility.showLog("Error applying threshold: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private int findOtsuThreshold(int[] histogram, int totalPixels) {
//        try {
//            float sum = 0;
//            for (int i = 0; i < 256; i++) {
//                sum += i * histogram[i];
//            }
//
//            float sumB = 0;
//            int wB = 0;
//            int wF = 0;
//            float varMax = 0;
//            int threshold = 0;
//
//            for (int i = 0; i < 256; i++) {
//                wB += histogram[i];
//                if (wB == 0) continue;
//
//                wF = totalPixels - wB;
//                if (wF == 0) break;
//
//                sumB += (float) (i * histogram[i]);
//
//                float mB = sumB / wB;
//                float mF = (sum - sumB) / wF;
//
//                float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
//
//                if (varBetween > varMax) {
//                    varMax = varBetween;
//                    threshold = i;
//                }
//            }
//
//            return threshold;
//        } catch (Exception e) {
//            Utility.showLog("Error finding threshold: " + e.getMessage());
//            return 128; // Default threshold
//        }
//    }
//
//    private Bitmap morphologicalClean(Bitmap original) {
//        try {
//            // Apply erosion followed by dilation to clean up noise
//            Bitmap eroded = applyErosion(original);
//            Bitmap cleaned = applyDilation(eroded);
//
//            if (eroded != original) eroded.recycle();
//            return cleaned;
//        } catch (Exception e) {
//            Utility.showLog("Error in morphological cleaning: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap applyErosion(Bitmap original) {
//        try {
//            int width = original.getWidth();
//            int height = original.getHeight();
//            int[] pixels = new int[width * height];
//            original.getPixels(pixels, 0, width, 0, 0, width, height);
//
//            int[] result = new int[width * height];
//
//            for (int y = 1; y < height - 1; y++) {
//                for (int x = 1; x < width - 1; x++) {
//                    int index = y * width + x;
//
//                    // 3x3 erosion kernel
//                    boolean allWhite = true;
//                    for (int dy = -1; dy <= 1; dy++) {
//                        for (int dx = -1; dx <= 1; dx++) {
//                            int neighborIndex = (y + dy) * width + (x + dx);
//                            if (Color.red(pixels[neighborIndex]) < 128) {
//                                allWhite = false;
//                                break;
//                            }
//                        }
//                        if (!allWhite) break;
//                    }
//
//                    result[index] = allWhite ? Color.WHITE : Color.BLACK;
//                }
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(result, 0, width, 0, 0, width, height);
//            return bitmap;
//        } catch (Exception e) {
//            Utility.showLog("Error in erosion: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap applyDilation(Bitmap original) {
//        try {
//            int width = original.getWidth();
//            int height = original.getHeight();
//            int[] pixels = new int[width * height];
//            original.getPixels(pixels, 0, width, 0, 0, width, height);
//
//            int[] result = new int[width * height];
//
//            for (int y = 1; y < height - 1; y++) {
//                for (int x = 1; x < width - 1; x++) {
//                    int index = y * width + x;
//
//                    // 3x3 dilation kernel
//                    boolean anyWhite = false;
//                    for (int dy = -1; dy <= 1; dy++) {
//                        for (int dx = -1; dx <= 1; dx++) {
//                            int neighborIndex = (y + dy) * width + (x + dx);
//                            if (Color.red(pixels[neighborIndex]) > 128) {
//                                anyWhite = true;
//                                break;
//                            }
//                        }
//                        if (anyWhite) break;
//                    }
//
//                    result[index] = anyWhite ? Color.WHITE : Color.BLACK;
//                }
//            }
//
//            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//            bitmap.setPixels(result, 0, width, 0, 0, width, height);
//            return bitmap;
//        } catch (Exception e) {
//            Utility.showLog("Error in dilation: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap enhanceContrastAndGrayscale(Bitmap original) {
//        try {
//            Bitmap result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(result);
//
//            // Create high contrast grayscale color matrix
//            ColorMatrix colorMatrix = new ColorMatrix();
//            colorMatrix.setSaturation(0); // Convert to grayscale
//
//            // Enhance contrast
//            float contrast = 2.0f; // Increase contrast
//            float brightness = 20f; // Slight brightness increase
//            colorMatrix.postConcat(new ColorMatrix(new float[] {
//                    contrast, 0, 0, 0, brightness,
//                    0, contrast, 0, 0, brightness,
//                    0, 0, contrast, 0, brightness,
//                    0, 0, 0, 1, 0
//            }));
//
//            Paint paint = new Paint();
//            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
//            canvas.drawBitmap(original, 0, 0, paint);
//
//            return result;
//        } catch (Exception e) {
//            Utility.showLog("Error enhancing contrast: " + e.getMessage());
//            return original;
//        }
//    }
//
//    private Bitmap applySharpenFilter(Bitmap original) {
//        try {
//            Bitmap result = Bitmap.createBitmap(original.getWidth(), original.getHeight(), Bitmap.Config.ARGB_8888);
//
//            int width = original.getWidth();
//            int height = original.getHeight();
//            int[] pixels = new int[width * height];
//            original.getPixels(pixels, 0, width, 0, 0, width, height);
//
//            // Simple sharpening kernel
//            int[] sharpened = new int[width * height];
//            for (int y = 1; y < height - 1; y++) {
//                for (int x = 1; x < width - 1; x++) {
//                    int index = y * width + x;
//
//                    int center = Color.red(pixels[index]);
//                    int top = Color.red(pixels[(y - 1) * width + x]);
//                    int bottom = Color.red(pixels[(y + 1) * width + x]);
//                    int left = Color.red(pixels[y * width + (x - 1)]);
//                    int right = Color.red(pixels[y * width + (x + 1)]);
//
//                    // Sharpening filter: 5*center - top - bottom - left - right
//                    int newValue = Math.max(0, Math.min(255, 5 * center - top - bottom - left - right));
//                    sharpened[index] = Color.rgb(newValue, newValue, newValue);
//                }
//            }
//
//            result.setPixels(sharpened, 0, width, 0, 0, width, height);
//            return result;
//        } catch (Exception e) {
//            Utility.showLog("Error applying sharpen filter: " + e.getMessage());
//            return original;
//        }
//    }
//
//    // ENHANCED OCR EXTRACTION METHODS
//    private void extractTemperatureAndHumidity(OcrResult result) {
//        try {
//            String text = result.rawText;
//
//            // Clean the text - remove extra spaces and normalize
//            String cleanedText = text.replaceAll("\\s+", " ").trim();
//
//            // Enhanced temperature patterns with more variations for digital displays
//            Pattern[] tempPatterns = {
//                    // Direct temperature with units - 25.0°C, 25°C, 25.0C
//                    Pattern.compile("([0-9]+\\.?[0-9]*)\\s*[°oO]?\\s*[CcFf]\\b", Pattern.CASE_INSENSITIVE),
//                    // Just degree symbol - 25.0°, 25°
//                    Pattern.compile("([0-9]+\\.?[0-9]*)\\s*[°oO]"),
//                    // Temperature keyword followed by number
//                    Pattern.compile("TEMP(?:ERATURE)?[:\\s]*([0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE),
//                    // Standalone decimal numbers in temperature range (common for digital displays)
//                    Pattern.compile("\\b([1-4][0-9]\\.?[0-9]?)\\b"),
//                    // Any number with decimal point in reasonable range
//                    Pattern.compile("\\b([0-9]{1,2}\\.[0-9]{1,2})\\b")
//            };
//
//            // Enhanced humidity patterns for digital displays
//            Pattern[] humidityPatterns = {
//                    // Standard humidity with % - 39%, 45%
//                    Pattern.compile("([0-9]+\\.?[0-9]*)\\s*%"),
//                    // Humidity keyword followed by number
//                    Pattern.compile("HUM(?:IDITY)?[:\\s]*([0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE),
//                    // RH (Relative Humidity) pattern
//                    Pattern.compile("RH[:\\s]*([0-9]+\\.?[0-9]*)", Pattern.CASE_INSENSITIVE),
//                    // Numbers in humidity range without % (common OCR miss)
//                    Pattern.compile("\\b([2-9][0-9]|100)\\b")
//            };
//
//            // First pass: Try direct pattern matching
//            extractWithPatterns(result, cleanedText, tempPatterns, humidityPatterns);
//
//            // Second pass: If we didn't find both values, try line-by-line analysis
//            if (result.temperature.isEmpty() || result.humidity.isEmpty()) {
//                extractFromLines(result, text);
//            }
//
//            // Third pass: Digital display layout analysis
//            if (result.temperature.isEmpty() || result.humidity.isEmpty()) {
//                extractFromDigitalDisplayLayout(result, text);
//            }
//
//            // Fourth pass: Extract any reasonable numbers if still missing
//            if (result.temperature.isEmpty() || result.humidity.isEmpty()) {
//                extractReasonableValues(result, text);
//            }
//
//            // Format the results
//            formatResults(result);
//
//        } catch (Exception e) {
//            Utility.showLog("Error in extractTemperatureAndHumidity: " + e.getMessage());
//        }
//    }
//
//    private void extractWithPatterns(OcrResult result, String text, Pattern[] tempPatterns, Pattern[] humidityPatterns) {
//        try {
//            // Extract temperature
//            for (Pattern pattern : tempPatterns) {
//                java.util.regex.Matcher matcher = pattern.matcher(text);
//                if (matcher.find()) {
//                    String tempValue = matcher.group(1);
//                    double temp = Double.parseDouble(tempValue);
//                    // Reasonable temperature range check (-50 to 100°C)
//                    if (temp >= -50 && temp <= 100) {
//                        result.temperature = tempValue;
//                        break;
//                    }
//                }
//            }
//
//            // Extract humidity
//            for (Pattern pattern : humidityPatterns) {
//                java.util.regex.Matcher matcher = pattern.matcher(text);
//                if (matcher.find()) {
//                    String humValue = matcher.group(1);
//                    double hum = Double.parseDouble(humValue);
//                    // Humidity range check (0-100%)
//                    if (hum >= 0 && hum <= 100) {
//                        result.humidity = humValue;
//                        break;
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in extractWithPatterns: " + e.getMessage());
//        }
//    }
//
//    private void extractFromLines(OcrResult result, String text) {
//        try {
//            String[] lines = text.split("[\\n\\r]+");
//
//            for (String line : lines) {
//                line = line.trim();
//                if (line.isEmpty()) continue;
//
//                Utility.showLog("Analyzing line: " + line);
//
//                // Look for temperature indicators
//                if (result.temperature.isEmpty()) {
//                    Pattern numberPattern = Pattern.compile("([0-9]+\\.?[0-9]*)");
//                    java.util.regex.Matcher matcher = numberPattern.matcher(line);
//                    if (matcher.find()) {
//                        String value = matcher.group(1);
//                        double temp = Double.parseDouble(value);
//                        // Check if it's in temperature range and line contains temp indicators
//                        if (temp >= -50 && temp <= 100 &&
//                                (line.contains("°") || line.toLowerCase().contains("c") ||
//                                        line.toLowerCase().contains("temp") || (temp >= 15 && temp <= 45))) {
//                            result.temperature = value;
//                        }
//                    }
//                }
//
//                // Look for humidity indicators
//                if (result.humidity.isEmpty()) {
//                    Pattern numberPattern = Pattern.compile("([0-9]+\\.?[0-9]*)");
//                    java.util.regex.Matcher matcher = numberPattern.matcher(line);
//                    if (matcher.find()) {
//                        String value = matcher.group(1);
//                        double hum = Double.parseDouble(value);
//                        // Check if it's in humidity range
//                        if (hum >= 0 && hum <= 100 &&
//                                (line.contains("%") || line.toLowerCase().contains("hum") ||
//                                        line.toLowerCase().contains("rh") || (hum >= 20 && hum <= 90))) {
//                            result.humidity = value;
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in extractFromLines: " + e.getMessage());
//        }
//    }
//
//    private void extractFromDigitalDisplayLayout(OcrResult result, String text) {
//        try {
//            // For digital displays, look for all numbers and assign based on typical ranges
//            Pattern allNumbers = Pattern.compile("([0-9]+\\.?[0-9]*)");
//            java.util.regex.Matcher matcher = allNumbers.matcher(text);
//
//            List<String> numbers = new ArrayList<>();
//            while (matcher.find()) {
//                numbers.add(matcher.group(1));
//            }
//
//            Utility.showLog("Found numbers: " + numbers.toString());
//
//            if (numbers.size() >= 2) {
//                for (String num : numbers) {
//                    try {
//                        double value = Double.parseDouble(num);
//
//                        // Assign to temperature if empty and in typical temperature range
//                        if (result.temperature.isEmpty() && value >= 15 && value <= 45) {
//                            result.temperature = num;
//                        }
//                        // Assign to humidity if empty and in typical humidity range
//                        else if (result.humidity.isEmpty() && value >= 20 && value <= 90) {
//                            result.humidity = num;
//                        }
//                    } catch (NumberFormatException e) {
//                        continue;
//                    }
//                }
//
//                // If we still don't have both and have exactly 2 numbers, assign by position
//                if ((result.temperature.isEmpty() || result.humidity.isEmpty()) && numbers.size() == 2) {
//                    try {
//                        double first = Double.parseDouble(numbers.get(0));
//                        double second = Double.parseDouble(numbers.get(1));
//
//                        // Usually temperature comes first in display
//                        if (result.temperature.isEmpty() && first >= 0 && first <= 60) {
//                            result.temperature = numbers.get(0);
//                        }
//                        if (result.humidity.isEmpty() && second >= 0 && second <= 100) {
//                            result.humidity = numbers.get(1);
//                        }
//                    } catch (NumberFormatException e) {
//                        // Continue with other methods
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in extractFromDigitalDisplayLayout: " + e.getMessage());
//        }
//    }
//
//    private void extractReasonableValues(OcrResult result, String text) {
//        try {
//            // Last resort: find any reasonable numbers
//            Pattern numberPattern = Pattern.compile("([0-9]+\\.?[0-9]*)");
//            java.util.regex.Matcher matcher = numberPattern.matcher(text);
//
//            while (matcher.find()) {
//                try {
//                    String numStr = matcher.group(1);
//                    double value = Double.parseDouble(numStr);
//
//                    // Assign to temperature if empty and in reasonable range
//                    if (result.temperature.isEmpty() && value >= 0 && value <= 60) {
//                        result.temperature = numStr;
//                    }
//                    // Assign to humidity if empty and in reasonable range
//                    else if (result.humidity.isEmpty() && value >= 0 && value <= 100) {
//                        result.humidity = numStr;
//                    }
//
//                    // Stop if we have both
//                    if (!result.temperature.isEmpty() && !result.humidity.isEmpty()) {
//                        break;
//                    }
//                } catch (NumberFormatException e) {
//                    continue;
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in extractReasonableValues: " + e.getMessage());
//        }
//    }
//
//    private void formatResults(OcrResult result) {
//        try {
//            // Format temperature with unit
//            if (!result.temperature.isEmpty()) {
//                // Add °C if not already present
//                if (!result.temperature.contains("°") && !result.temperature.toLowerCase().contains("c")) {
//                    result.temperature = result.temperature + "°C";
//                }
//            }
//
//            // Format humidity with %
//            if (!result.humidity.isEmpty()) {
//                // Add % if not already present
//                if (!result.humidity.contains("%")) {
//                    result.humidity = result.humidity + "%";
//                }
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error in formatResults: " + e.getMessage());
//        }
//    }
//
//    // CLEANUP
//    public void destroy() {
//        try {
//            if (tessBaseApi != null) {
//                tessBaseApi.recycle();
//                tessBaseApi = null;
//                isTesseractInitialized = false;
//            }
//        } catch (Exception e) {
//            Utility.showLog("Error destroying Tesseract: " + e.getMessage());
//        }
//    }
//}