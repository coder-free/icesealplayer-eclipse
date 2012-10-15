package com.zbf.iceseal.service;
import com.zbf.iceseal.service.IMusicImportServiceCallback;
interface IMusicImportService {
	void registerCallback(IMusicImportServiceCallback callback);
	void unregisterCallback(IMusicImportServiceCallback callback);
	void stopImport();
}