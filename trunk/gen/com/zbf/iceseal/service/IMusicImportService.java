/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\MySrc\\Eclipse\\new_work\\IceSeal\\src\\com\\zbf\\iceseal\\service\\IMusicImportService.aidl
 */
package com.zbf.iceseal.service;
public interface IMusicImportService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zbf.iceseal.service.IMusicImportService
{
private static final java.lang.String DESCRIPTOR = "com.zbf.iceseal.service.IMusicImportService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zbf.iceseal.service.IMusicImportService interface,
 * generating a proxy if needed.
 */
public static com.zbf.iceseal.service.IMusicImportService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zbf.iceseal.service.IMusicImportService))) {
return ((com.zbf.iceseal.service.IMusicImportService)iin);
}
return new com.zbf.iceseal.service.IMusicImportService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.zbf.iceseal.service.IMusicImportServiceCallback _arg0;
_arg0 = com.zbf.iceseal.service.IMusicImportServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.zbf.iceseal.service.IMusicImportServiceCallback _arg0;
_arg0 = com.zbf.iceseal.service.IMusicImportServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopImport:
{
data.enforceInterface(DESCRIPTOR);
this.stopImport();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zbf.iceseal.service.IMusicImportService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void registerCallback(com.zbf.iceseal.service.IMusicImportServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(com.zbf.iceseal.service.IMusicImportServiceCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void stopImport() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopImport, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopImport = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void registerCallback(com.zbf.iceseal.service.IMusicImportServiceCallback callback) throws android.os.RemoteException;
public void unregisterCallback(com.zbf.iceseal.service.IMusicImportServiceCallback callback) throws android.os.RemoteException;
public void stopImport() throws android.os.RemoteException;
}
