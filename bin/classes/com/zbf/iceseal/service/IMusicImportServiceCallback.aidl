package com.zbf.iceseal.service;

interface IMusicImportServiceCallback {
    void onImportStart();
    void onImportComplete(int count);
    void onProgressUpdate(String message);
}
