/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: G:\\MySrc\\Eclipse\\new_work\\IceSeal\\src\\com\\zbf\\iceseal\\service\\IMusicImportServiceCallback.aidl
 */
package com.zbf.iceseal.service;
public interface IMusicImportServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.zbf.iceseal.service.IMusicImportServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.zbf.iceseal.service.IMusicImportServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.zbf.iceseal.service.IMusicImportServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.zbf.iceseal.service.IMusicImportServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.zbf.iceseal.service.IMusicImportServiceCallback))) {
return ((com.zbf.iceseal.service.IMusicImportServiceCallback)iin);
}
return new com.zbf.iceseal.service.IMusicImportServiceCallback.Stub.Proxy(obj);
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
case TRANSACTION_onImportStart:
{
data.enforceInterface(DESCRIPTOR);
this.onImportStart();
reply.writeNoException();
return true;
}
case TRANSACTION_onImportComplete:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.onImportComplete(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onProgressUpdate:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onProgressUpdate(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.zbf.iceseal.service.IMusicImportServiceCallback
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
public void onImportStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_onImportStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onImportComplete(int count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(count);
mRemote.transact(Stub.TRANSACTION_onImportComplete, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onProgressUpdate(java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_onProgressUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onImportStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onImportComplete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onProgressUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public void onImportStart() throws android.os.RemoteException;
public void onImportComplete(int count) throws android.os.RemoteException;
public void onProgressUpdate(java.lang.String message) throws android.os.RemoteException;
}
