//package com.hasanjaved.reportmate.model;
//
//public class TripTestImages {
//
//    // Method 1: Using AsyncTask (Deprecated but still widely used)
//    private class GenerateReportTask extends AsyncTask<Report, Void, Boolean> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Show progress dialog or loading indicator
//            // progressDialog.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Report... reports) {
//            try {
//                generateReportFile(reports[0]);
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            super.onPostExecute(success);
//            // Hide progress dialog and handle result
//            // progressDialog.dismiss();
//            if (success) {
//                // Handle success
//                Toast.makeText(context, "Report generated successfully", Toast.LENGTH_SHORT).show();
//            } else {
//                // Handle error
//                Toast.makeText(context, "Failed to generate report", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//// Usage:
//// new GenerateReportTask().execute(report);
//
//// ===================================================================
//
//    // Method 2: Using Executor with Handler (Recommended for modern Android)
//    private void generateReportAsync(Report report) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());
//
//        executor.execute(() -> {
//            // Background thread
//            boolean success = false;
//            try {
//                generateReportFile(report);
//                success = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Switch back to main thread for UI updates
//            boolean finalSuccess = success;
//            handler.post(() -> {
//                if (finalSuccess) {
//                    Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//    }
//
//// Usage:
//// generateReportAsync(report);
//
//// ===================================================================
//
//    // Method 3: Using CompletableFuture (Android API 24+)
//    private void generateReportWithCompletableFuture(Report report) {
//        CompletableFuture.supplyAsync(() -> {
//            try {
//                generateReportFile(report);
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        }).thenAcceptAsync(success -> {
//            // This runs on main thread
//            runOnUiThread(() -> {
//                if (success) {
//                    Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//    }
//
//// Usage:
//// generateReportWithCompletableFuture(report);
//
//// ===================================================================
//
//    // Method 4: Using Thread with Handler
//    private void generateReportWithThread(Report report) {
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//
//        Thread thread = new Thread(() -> {
//            boolean success = false;
//            try {
//                generateReportFile(report);
//                success = true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Post result to main thread
//            boolean finalSuccess = success;
//            mainHandler.post(() -> {
//                if (finalSuccess) {
//                    Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//
//        thread.start();
//    }
//
//// Usage:
//// generateReportWithThread(report);
//
//// ===================================================================
//
//    // Method 5: Using Callable with ExecutorService and Future
//    private void generateReportWithCallable(Report report) {
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//
//        Callable<Boolean> callable = () -> {
//            try {
//                generateReportFile(report);
//                return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//                return false;
//            }
//        };
//
//        Future<Boolean> future = executor.submit(callable);
//
//        // Optional: Get result if needed
//        executor.execute(() -> {
//            try {
//                Boolean result = future.get(); // This will block until completion
//
//                // Update UI on main thread
//                runOnUiThread(() -> {
//                    if (result) {
//                        Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//// Usage:
//// generateReportWithCallable(report);
//
//// ===================================================================
//
//    // Method 6: Using RxJava (if you're using RxJava in your project)
//    private void generateReportWithRxJava(Report report) {
//        Observable.fromCallable(() -> {
//                    generateReportFile(report);
//                    return true;
//                })
//                .subscribeOn(Schedulers.io()) // Background thread
//                .observeOn(AndroidSchedulers.mainThread()) // Main thread for result
//                .subscribe(
//                        success -> {
//                            // Success
//                            Toast.makeText(this, "Report generated successfully", Toast.LENGTH_SHORT).show();
//                        },
//                        error -> {
//                            // Error
//                            error.printStackTrace();
//                            Toast.makeText(this, "Failed to generate report", Toast.LENGTH_SHORT).show();
//                        }
//                );
//    }
//
//// Usage:
//// generateReportWithRxJava(report);
//
//// ===================================================================
//
//    // Method 7: Simple Fire-and-Forget (No callback)
//    private void generateReportFireAndForget(Report report) {
//        new Thread(() -> {
//            try {
//                generateReportFile(report);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//// Usage:
//// generateReportFireAndForget(report);
//
//// ===================================================================
//
//    // Method 8: Using AsyncTask with Interface Callback (More flexible)
//    public interface ReportGenerationListener {
//        void onReportGenerationStarted();
//        void onReportGenerationCompleted(boolean success);
//        void onReportGenerationError(Exception e);
//    }
//
//    private class GenerateReportTaskWithCallback extends AsyncTask<Report, Void, Boolean> {
//        private ReportGenerationListener listener;
//        private Exception exception;
//
//        public GenerateReportTaskWithCallback(ReportGenerationListener listener) {
//            this.listener = listener;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (listener != null) {
//                listener.onReportGenerationStarted();
//            }
//        }
//
//        @Override
//        protected Boolean doInBackground(Report... reports) {
//            try {
//                generateReportFile(reports[0]);
//                return true;
//            } catch (Exception e) {
//                this.exception = e;
//                return false;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Boolean success) {
//            super.onPostExecute(success);
//            if (listener != null) {
//                if (success) {
//                    listener.onReportGenerationCompleted(true);
//                } else {
//                    listener.onReportGenerationError(exception);
//                }
//            }
//        }
//    }
//
//// Usage with callback:
///*
//new GenerateReportTaskWithCallback(new ReportGenerationListener() {
//    @Override
//    public void onReportGenerationStarted() {
//        // Show loading
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void onReportGenerationCompleted(boolean success) {
//        // Hide loading and show success
//        progressBar.setVisibility(View.GONE);
//        Toast.makeText(MainActivity.this, "Report generated!", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onReportGenerationError(Exception e) {
//        // Hide loading and show error
//        progressBar.setVisibility(View.GONE);
//        Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//    }
//}).execute(report);
//*/
//
//}
