/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.9
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package org.doubango.tinyWRAP;

public class SipUri {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected SipUri(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(SipUri obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        tinyWRAPJNI.delete_SipUri(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public SipUri(String uriString, String displayName) {
    this(tinyWRAPJNI.new_SipUri__SWIG_0(uriString, displayName), true);
  }

  public SipUri(String uriString) {
    this(tinyWRAPJNI.new_SipUri__SWIG_1(uriString), true);
  }

  public static boolean isValid(String arg0) {
    return tinyWRAPJNI.SipUri_isValid__SWIG_0(arg0);
  }

  public boolean isValid() {
    return tinyWRAPJNI.SipUri_isValid__SWIG_1(swigCPtr, this);
  }

  public String getScheme() {
    return tinyWRAPJNI.SipUri_getScheme(swigCPtr, this);
  }

  public String getHost() {
    return tinyWRAPJNI.SipUri_getHost(swigCPtr, this);
  }

  public int getPort() {
    return tinyWRAPJNI.SipUri_getPort(swigCPtr, this);
  }

  public String getUserName() {
    return tinyWRAPJNI.SipUri_getUserName(swigCPtr, this);
  }

  public String getPassword() {
    return tinyWRAPJNI.SipUri_getPassword(swigCPtr, this);
  }

  public String getDisplayName() {
    return tinyWRAPJNI.SipUri_getDisplayName(swigCPtr, this);
  }

  public String getParamValue(String pname) {
    return tinyWRAPJNI.SipUri_getParamValue(swigCPtr, this, pname);
  }

  public void setDisplayName(String displayName) {
    tinyWRAPJNI.SipUri_setDisplayName(swigCPtr, this, displayName);
  }

}
