//public void generateReportFile(Report report) {
//        String reportName = report.getProjectNo();
//
//        // Show loading dialog
//        ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Generating Report");
//        progressDialog.setMessage("Initializing...");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setMax(100);
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        // Use AsyncTask or WorkManager for better thread management
//        new ReportGenerationTask(this, progressDialog).execute(reportName, report);
//        }
//
//private static class ReportGenerationTask extends AsyncTask<Object, ProgressUpdate, String> {
//    private WeakReference<Context> contextRef;
//    private WeakReference<ProgressDialog> dialogRef;
//    private String errorMessage;
//
//    ReportGenerationTask(Context context, ProgressDialog dialog) {
//        this.contextRef = new WeakReference<>(context);
//        this.dialogRef = new WeakReference<>(dialog);
//    }
//
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//        publishProgress(new ProgressUpdate(0, "Starting report generation..."));
//    }
//
//    @Override
//    protected String doInBackground(Object... params) {
//        try {
//            String reportName = (String) params[0];
//            Report report = (Report) params[1];
//            Context context = contextRef.get();
//
//            if (context == null) return null;
//
//            // Generate report synchronously in background thread
//            publishProgress(new ProgressUpdate(10, "Creating document..."));
//
//            XWPFDocument document = new XWPFDocument();
//
//            publishProgress(new ProgressUpdate(20, "Adding titles..."));
//            createDocumentTitles(document, report);
//
//            publishProgress(new ProgressUpdate(30, "Creating main table..."));
//            createExactReportTable(document, report);
//
//            publishProgress(new ProgressUpdate(60, "Processing images..."));
//
//            // Prepare image arrays
//            String[] dbBoxTitles = {
//                    "Temperature & Humidity", "Panel", "Panel inside", "Additional View"
//            };
//
//            String[] dbBoxImages = {
//                    "ReportMate/" + reportName + "/" + Utility.generalImageTemperature + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelFront + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelInside + ".jpg",
//                    "ReportMate/" + reportName + "/" + Utility.dbBoxPanelNameplate + ".jpg"
//            };
//
//            String[] irTestLabels = {
//                    "IR Test Connection (A-G)", "IR Test Result (A-G)",
//                    "IR Test Connection (B-G)", "IR Test Result (B-G)",
//                    "IR Test Connection (C-G)", "IR Test Result (C-G)",
//                    "IR Test Connection (A-B)", "IR Test Result (A-B)",
//                    "IR Test Connection (B-C)", "IR Test Result (B-C)",
//                    "IR Test Connection (C-A)", "IR Test Result (C-A)"
//            };
//
//            String irLinkBase = "ReportMate/" + reportName + "/" + Utility.IrTest + "/";
//            String[] irTestImages = {
//                    irLinkBase + Utility.imgAgConnection + ".jpg", irLinkBase + Utility.imgAgResult + ".jpg",
//                    irLinkBase + Utility.imgBgConnection + ".jpg", irLinkBase + Utility.imgBgResult + ".jpg",
//                    irLinkBase + Utility.imgCgConnection + ".jpg", irLinkBase + Utility.imgCgResult + ".jpg",
//                    irLinkBase + Utility.imgAbConnection + ".jpg", irLinkBase + Utility.imgAbResult + ".jpg",
//                    irLinkBase + Utility.imgBcConnection + ".jpg", irLinkBase + Utility.imgBcResult + ".jpg",
//                    irLinkBase + Utility.imgCaConnection + ".jpg", irLinkBase + Utility.imgCaResult + ".jpg"
//            };
//
//            // Add images with progress updates
//            publishProgress(new ProgressUpdate(70, "Adding panel images..."));
//            addImageSectionWithProgress(document, "Panel general images", dbBoxImages, dbBoxTitles);
//
//            publishProgress(new ProgressUpdate(80, "Adding IR test images..."));
//            addImageSectionWithProgress(document, "MCCB & MCB: IR Test and Results", irTestImages, irTestLabels);
//
//            publishProgress(new ProgressUpdate(90, "Saving document..."));
//
//            // Save document
//            String savedPath = saveDocumentSync(context, document, reportName);
//
//            publishProgress(new ProgressUpdate(100, "Report completed!"));
//
//            return savedPath;
//
//        } catch (Exception e) {
//            Log.e("ReportGeneration", "Error generating report", e);
//            errorMessage = e.getMessage();
//            return null;
//        }
//    }
//
//    @Override
//    protected void onProgressUpdate(ProgressUpdate... values) {
//        super.onProgressUpdate(values);
//        ProgressDialog dialog = dialogRef.get();
//        if (dialog != null && values.length > 0) {
//            ProgressUpdate update = values[0];
//            dialog.setProgress(update.progress);
//            dialog.setMessage(update.message);
//        }
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//        super.onPostExecute(result);
//
//        ProgressDialog dialog = dialogRef.get();
//        Context context = contextRef.get();
//
//        if (dialog != null) {
//            dialog.dismiss();
//        }
//
//        if (context != null) {
//            if (result != null) {
//                Utility.showLog("Report generated successfully: " + result);
//                Toast.makeText(context, "Report saved successfully!", Toast.LENGTH_LONG).show();
//            } else {
//                Utility.showLog("Failed to generate report: " + errorMessage);
//                Toast.makeText(context, "Failed to generate report: " + errorMessage, Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    // Helper method to add images with smaller batches and memory management
//    private void addImageSectionWithProgress(XWPFDocument document, String title, String[] imagePaths, String[] imageNames) {
//        try {
//            // Add section heading
//            XWPFParagraph imageSectionPara = document.createParagraph();
//            imageSectionPara.setAlignment(ParagraphAlignment.LEFT);
//            imageSectionPara.setSpacingAfter(200);
//            imageSectionPara.setSpacingBefore(200);
//            XWPFRun imageSectionRun = imageSectionPara.createRun();
//            imageSectionRun.setText("6.2    DB-3.1 " + title);
//            imageSectionRun.setBold(true);
//            imageSectionRun.setFontFamily("Arial");
//            imageSectionRun.setFontSize(12);
//            imageSectionRun.setColor("00B050");
//
//            // Create table for images
//            XWPFTable imageTable = document.createTable();
//            setTableBorders(imageTable);
//            setTableWidth(imageTable, 100);
//
//            // Process images in smaller batches
//            for (int i = 0; i < imagePaths.length; i += 2) {
//                // Create image row
//                XWPFTableRow imageRow;
//                if (i == 0) {
//                    imageRow = imageTable.getRow(0);
//                } else {
//                    imageRow = imageTable.createRow();
//                }
//
//                while (imageRow.getTableCells().size() < 2) {
//                    imageRow.addNewTableCell();
//                }
//
//                // Add images with smaller size to reduce memory usage
//                addImageToCellOptimized(imageRow.getCell(0), imagePaths[i], 150, 112);
//
//                if (i + 1 < imagePaths.length) {
//                    addImageToCellOptimized(imageRow.getCell(1), imagePaths[i + 1], 150, 112);
//                } else {
//                    setCellText(imageRow.getCell(1), "", false, ParagraphAlignment.CENTER);
//                }
//
//                // Create caption row
//                XWPFTableRow captionRow = imageTable.createRow();
//                while (captionRow.getTableCells().size() < 2) {
//                    captionRow.addNewTableCell();
//                }
//
//                setCellText(captionRow.getCell(0), imageNames[i], true, ParagraphAlignment.CENTER);
//                if (i + 1 < imageNames.length) {
//                    setCellText(captionRow.getCell(1), imageNames[i + 1], true, ParagraphAlignment.CENTER);
//                } else {
//                    setCellText(captionRow.getCell(1), "", false, ParagraphAlignment.CENTER);
//                }
//
//                // Force garbage collection every 2 images
//                if (i % 2 == 0) {
//                    System.gc();
//                    // Small delay to allow GC to complete
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        Thread.currentThread().interrupt();
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            Log.e("ReportGeneration", "Error adding image section", e);
//        }
//    }
//
//    // More memory-efficient image loading
//    private void addImageToCellOptimized(XWPFTableCell cell, String imagePath, int width, int height) {
//        FileInputStream imageStream = null;
//        ByteArrayOutputStream baos = null;
//        ByteArrayInputStream bais = null;
//
//        try {
//            cell.removeParagraph(0);
//            XWPFParagraph para = cell.addParagraph();
//            para.setAlignment(ParagraphAlignment.CENTER);
//            XWPFRun run = para.createRun();
//
//            File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), imagePath);
//
//            if (!imageFile.exists()) {
//                imageFile = new File(imagePath);
//            }
//
//            if (imageFile.exists() && imageFile.length() > 0 && imageFile.length() < 10 * 1024 * 1024) { // Max 10MB per image
//                imageStream = new FileInputStream(imageFile);
//
//                String fileName = imageFile.getName().toLowerCase();
//                int pictureType = fileName.endsWith(".png") ? XWPFDocument.PICTURE_TYPE_PNG : XWPFDocument.PICTURE_TYPE_JPEG;
//
//                // Use smaller buffer and process in chunks
//                baos = new ByteArrayOutputStream();
//                byte[] buffer = new byte[2048]; // Smaller buffer
//                int bytesRead;
//                while ((bytesRead = imageStream.read(buffer)) != -1) {
//                    baos.write(buffer, 0, bytesRead);
//                }
//
//                bais = new ByteArrayInputStream(baos.toByteArray());
//                run.addPicture(bais, pictureType, imageFile.getName(), Units.toEMU(width), Units.toEMU(height));
//
//            } else {
//                run.setText("Image not found or too large");
//                run.setFontFamily("Arial");
//                run.setFontSize(9);
//            }
//
//            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
//
//        } catch (Exception e) {
//            Log.e("ReportGeneration", "Error adding image: " + imagePath, e);
//            try {
//                cell.removeParagraph(0);
//                XWPFParagraph para = cell.addParagraph();
//                XWPFRun run = para.createRun();
//                run.setText("Error loading image");
//                run.setFontFamily("Arial");
//                run.setFontSize(9);
//            } catch (Exception ex) {
//                Log.e("ReportGeneration", "Error adding error text", ex);
//            }
//        } finally {
//            // Ensure all streams are closed
//            try { if (bais != null) bais.close(); } catch (Exception e) { }
//            try { if (baos != null) baos.close(); } catch (Exception e) { }
//            try { if (imageStream != null) imageStream.close(); } catch (Exception e) { }
//        }
//    }
//
//    // Helper class for progress updates
//    private static class ProgressUpdate {
//        int progress;
//        String message;
//
//        ProgressUpdate(int progress, String message) {
//            this.progress = progress;
//            this.message = message;
//        }
//    }
//
//    // Synchronous save method
//    private String saveDocumentSync(Context context, XWPFDocument document, String reportName) throws Exception {
//        String fileName = reportName + ".docx";
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            return saveWithMediaStoreSync(context, document, fileName);
//        } else {
//            return saveLegacySync(context, document, fileName);
//        }
//    }
//
//    private String saveWithMediaStoreSync(Context context, XWPFDocument document, String fileName) throws Exception {
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
//        values.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
//        values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/ReportMateReports");
//
//        ContentResolver resolver = context.getContentResolver();
//        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
//
//        if (uri != null) {
//            OutputStream outputStream = resolver.openOutputStream(uri);
//            document.write(outputStream);
//            outputStream.close();
//            document.close();
//            return "Documents/ReportMateReports/" + fileName;
//        }
//        throw new Exception("Failed to create file via MediaStore");
//    }
//
//    private String saveLegacySync(Context context, XWPFDocument document, String fileName) throws Exception {
//        File documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
//        File reportsDir = new File(documentsDir, "ReportMateReports");
//
//        if (!reportsDir.exists()) {
//            reportsDir.mkdirs();
//        }
//
//        File docFile = new File(reportsDir, fileName);
//        FileOutputStream out = new FileOutputStream(docFile);
//        document.write(out);
//        out.close();
//        document.close();
//
//        return docFile.getAbsolutePath();
//    }
//}