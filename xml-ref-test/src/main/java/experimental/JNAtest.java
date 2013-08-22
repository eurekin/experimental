package experimental;

import com.sun.deploy.net.DownloadEngine;
import com.sun.deploy.trace.Trace;
import com.sun.javaws.jnl.LaunchDesc;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

import javax.jnlp.IntegrationService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author gmatoga
 */
public class JNAtest {
    public interface IUnknown {
        int QueryInterface(
                Guid.GUID riid,
                PointerByReference ppvObject);
        int AddRef();
        int Release();

    }

    public interface IPropertyStore {
        int Commit();
        int GetAt();
        int GetCount(IntByReference cProps);
        int GetValue(PROPERTYKEY.ByReference propkey, PROPVARIANT.ByReference propvar);
        int SetValue(PROPERTYKEY.ByReference propkey, PROPVARIANT.ByReference propvar);
    }


    public static void main(String[] args) {
        final Kernel32b kernel32 = Kernel32b.INSTANCE;
        int pid = kernel32.GetCurrentProcessId();

//        System.out.println("Current process command line: " + commandLine);
        System.out.println("PID == " + pid);
        final JFrame frame = new JFrame("hi");
        frame.add(new JButton(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dorun(frame);
            }
        }));
        frame.setVisible(true);
        tryReallyHardToSetWindowsTaskbarProperties(frame);



//        final IntByReference cProps = new IntByReference();
//        propertyStore.GetCount(cProps);
//        System.out.println("PropCount = " + cProps);


        //enumWindows(user32);
    }

    public static void tryReallyHardToSetWindowsTaskbarProperties(JFrame frame) {
        final long windowID = Native.getWindowID(frame);
        final Pointer windowPointer = Native.getWindowPointer(frame);
        byte[] windowText2 = new byte[512];
        final User32 user32 = User32.INSTANCE;
        user32.GetWindowTextA(windowPointer, windowText2, 512);

        final Shell32 shell32 = Shell32.INSTANCE;
        final Ole32 ole32 = Ole32.INSTANCE;
        final String IID_IPropertyStoreString = "{886d8eeb-8cf2-4446-8d02-cdba1dbdcf99}";
        final Guid.GUID IID_IPropertyStore = Ole32Util.getGUIDFromString(IID_IPropertyStoreString);
        PointerByReference pp = new PointerByReference();
        shell32.SHGetPropertyStoreForWindow(windowPointer, IID_IPropertyStore, pp);

        System.out.println(pp);

        final Pointer interfacePointer = pp.getValue();
        final Pointer vTablePointer = interfacePointer.getPointer(0);
        final Pointer[] vTable = new Pointer[3];
        vTablePointer.read(0, vTable, 0, 3);
        final IUnknown iUnknown = new IUnknown() {
            public int QueryInterface(Guid.GUID riid, PointerByReference ppvObject) {
                Function f = Function.getFunction(vTable[0], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer, riid, ppvObject});
            }

            public int AddRef() {
                Function f = Function.getFunction(vTable[1], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer});
            }

            public int Release() {
                Function f = Function.getFunction(vTable[2], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer});
            }

        };
        final PointerByReference ppObj = new PointerByReference();
        final int i = iUnknown.QueryInterface(IID_IPropertyStore, ppObj);

        final Pointer interfacePointer2 = ppObj.getValue();
        final Pointer vTablePointer2 = interfacePointer2.getPointer(0);
        final Pointer[] vTable2 = new Pointer[8];
        vTablePointer2.read(0, vTable2, 0, 8);

        final IPropertyStore propertyStore = new IPropertyStore() {

            @Override
            public int GetCount(IntByReference cProps) {
                Function f = Function.getFunction(vTable2[3], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer, cProps});
            }

            @Override
            public int GetAt() {
                Function f = Function.getFunction(vTable2[4], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer});
            }

            @Override
            public int GetValue(PROPERTYKEY.ByReference propkey, PROPVARIANT.ByReference propvar) {
                Function f = Function.getFunction(vTable2[5], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer, propkey, propvar});
            }

            @Override
            public int SetValue(PROPERTYKEY.ByReference propkey, PROPVARIANT.ByReference propvar) {
                Function f = Function.getFunction(vTable2[6], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer, propkey, propvar});
            }
            @Override
            public int Commit() {
                Function f = Function.getFunction(vTable2[7], Function.ALT_CONVENTION);
                return f.invokeInt(new Object[]{interfacePointer});
            }


        };

        System.out.println("after first call. QueryInterFace returned " + i);

        iUnknown.AddRef();
        IntByReference pCount = new IntByReference();
        propertyStore.Commit();

        IntByReference pbr2 = new IntByReference();
        propertyStore.GetCount(pbr2);


        final PROPERTYKEY.ByReference propKeybyReference = new PROPERTYKEY.ByReference();
        final String AppUserModelID  = "{9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3}";
        propKeybyReference.fmtid = Ole32Util.getGUIDFromString(AppUserModelID);
        propKeybyReference.pid = 5;
        final PROPVARIANT.ByReference propVarByReference = new PROPVARIANT.ByReference();
        //propVarByReference.val = Pointer.NULL;
        propVarByReference.vt = Variant.VT_LPWSTR;
        int result = propertyStore.GetValue(propKeybyReference, propVarByReference);
        System.out.println("ValueOfAppUserModelId: " + propVarByReference + " result of get value: " + result);

        System.out.println("Property Count = " + pbr2.getValue() + " variant: " + propVarByReference.vt);
        System.out.println("Window id = " + windowID + " Text: " + Native.toString(windowText2));

        final String propertyValueToBeSet = "Test.HostedApp2";
        setStringPropertyOnGUID(propertyStore, propertyValueToBeSet, 5, AppUserModelID);

        // relaunch command
        final WString commandLine = Kernel32b.INSTANCE.GetCommandLineW();
        // String commandLineJNLP = "C:\\Windows\\System32\\javaws.exe -localfile -J-Djnlp.application.href=http://pacelibom-eurekin.rhcloud.com/app.jnlp \"C:\\Users\\gmatoga\\AppData\\LocalLow\\Sun\\Java\\Deployment\\cache\\6.0\\36\\d38cde4-69ef18af";
        System.out.println(commandLine.toString().replaceAll("\\s", "\n"));
        setStringPropertyOnGUID(propertyStore,  commandLine.toString(), 2, "{9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3}");

        // 9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3
        setStringPropertyOnGUID(propertyStore, "Experimental",4, "{9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3}");


    }

    private static void setStringPropertyOnGUID(IPropertyStore propertyStore, String propertyValueToBeSet, int pid, String appUserModelID) {
        int result;
        final PROPERTYKEY.ByReference propKeybyReference2 = new PROPERTYKEY.ByReference();
        propKeybyReference2.fmtid = Ole32Util.getGUIDFromString(appUserModelID);
        propKeybyReference2.pid = pid;
        final PROPVARIANT.ByReference propVarByReference2 = new PROPVARIANT.ByReference();
        final WString test = new WString(propertyValueToBeSet);
        propVarByReference2.val = test;
        propVarByReference2.vt = Variant.VT_LPWSTR;
        System.out.println("argument: " + propVarByReference2);
        result = propertyStore.SetValue(propKeybyReference2, propVarByReference2);
        System.out.println("set value result: " + Integer.toHexString(result));
    }

    private static void dorun(JFrame frame) {
        tryReallyHardToSetWindowsTaskbarProperties(frame);
    }

    /**
     * Common error codes
     * http://msdn.microsoft.com/en-us/library/windows/desktop/aa378137(v=vs.85).aspx
     *
     * One or more arguments are not valid
     *
     * ? 8007007a
     */
public static final int E_INVALIDARG = 0x80070057;

    public interface  Shell32 extends StdCallLibrary {
        Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class);

        WinNT.HRESULT SHGetPropertyStoreForWindow(Pointer hWnd, Guid.GUID guid, PointerByReference ppv);
    }



    public interface  Kernel32b extends Kernel32 {
        Kernel32b INSTANCE = (Kernel32b) Native.loadLibrary("kernel32", Kernel32b.class);

        WString GetCommandLineW();
    }



    // Equivalent JNA mappings
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

        int GetWindowTextA(Pointer hWnd, byte[] lpString, int nMaxCount);
        int GetWindowThreadProcessId(Pointer hWnd, IntByReference lpDword);

        interface WNDENUMPROC extends StdCallCallback {
            boolean callback(Pointer hWnd, Pointer arg);
        }
    }

    public interface Ole32 extends  StdCallLibrary {
        Ole32 INSTANCE = (Ole32) Native.loadLibrary("ole32", Ole32.class);
    }


//        typedef struct {
//            GUID  fmtid;
//            DWORD pid;
//        } PROPERTYKEY;

    public static class PROPERTYKEY extends Structure {
        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"fmtid", "pid"});
        }

        public static class ByReference extends PROPERTYKEY implements Structure.ByReference {}
        public Guid.GUID fmtid;
        public int pid;
    }

//        typedef struct PROPVARIANT {
//            VARTYPE vt;
//            WORD    wReserved1;
//            WORD    wReserved2;
//            WORD    wReserved3;
//            union {
//                LPSTR             pszVal;
//                LPWSTR            pwszVal;
//            };
//        } PROPVARIANT

    // VT TYPES http://msdn.microsoft.com/en-us/library/aa380072(v=vs.85).aspx

    public static class PROPVARIANT extends Structure {
        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"vt", "r1","r2","r3","val"});
        }

        public static class ByReference extends PROPVARIANT implements Structure.ByReference {}
        public int vt;
        public byte r1;
        public byte r2;
        public byte r3;
        public WString val;
    }
}

