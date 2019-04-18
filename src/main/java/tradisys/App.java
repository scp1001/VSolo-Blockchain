package tradisys;

import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.List;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;


import java.io.FileWriter;



import java.io.File;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;


import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * A JNA based driver for reading single characters from the console.
 *
 * <p>This class is used for console mode programs.
 * It supports non-blocking reads of single key strokes without echo.
 */

class Blockchain{
    String currentBlock = "";

    public String readBlock(int num) throws FileNotFoundException{
        String data = "";
        File file = new File("Blockchain/" + Integer.toString(num) + ".txt");
        if (file.exists()) {
            Scanner sc = new Scanner(file);
            //System.out.println("File_lines: ");

            String height = sc.nextLine();
            data = sc.nextLine();
            String hash = sc.nextLine();
        }else{
            data = "Block not exists";
        }
        return data;
    }

    public String readBlockchain() throws FileNotFoundException{
        File directory=new File("Blockchain");
        int fileCount=directory.list().length;
        String data = "";

        for (int i = 0; i < fileCount; i++) {
            data += "Block " + Integer.toString(i) + ": " + readBlock(i) + "\n";
        }

        return data;
    }

    //Determining number of new block with simple count files is potentially dangerous as any other file in folder can broke it. TODO: upgrade
    private void newBlock(String data) throws FileNotFoundException{
        File directory=new File("Blockchain");
        int fileCount=directory.list().length;
        //System.out.println("File Count:"+fileCount);
        File outputFile;
        FileWriter fileWriter;
        outputFile  = new File("Blockchain/" + Integer.toString(fileCount) + ".txt");

        String lastLine= "";
        if (fileCount > 0){
            File file = new File("Blockchain/" + Integer.toString(fileCount-1) + ".txt");
            Scanner sc = new Scanner(file);
            //System.out.println("File_lines: ");
            while (sc.hasNextLine()) {
                lastLine = sc.nextLine();
            }
        }


        String input =  lastLine.trim() + Integer.toString(fileCount) + data;
        SHA3.DigestSHA3 digestSHA3 = new SHA3.Digest256();
        byte[] digest = digestSHA3.digest(input.getBytes());

        String hash= Hex.toHexString(digest);

        //System.out.println("input:" + input);
        System.out.println("\nBlock " + fileCount + " with hash " + hash + " saved !\n");

        try {
            outputFile.createNewFile();


            fileWriter = new FileWriter(outputFile, false);

            fileWriter.write(Integer.toString(fileCount) + "\n" + data + "\n" + hash);

            fileWriter.close();
        }

        catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String data) throws FileNotFoundException{
        int size = 8;

        String pack = "";
        List<String> tokens = Lists.newArrayList(Splitter.fixedLength(size).split(this.currentBlock + data));
        this.currentBlock= "";
        for (int i = 0; i < tokens.size(); i++) {
            pack = tokens.get(i);
            if (pack.length() == 8) {
                newBlock(pack);
            }else{
                this.currentBlock=pack;
            }
        }

        if (pack.length() > 0){
            System.out.println("\nCurrent block in memory: " + pack + "\n");
        }

        /*
        System.out.println("Tokens: \n");
        System.out.println(tokens);
        System.out.println("Current Block: \n");
        System.out.println(currentBlock);
        */
    }

}

public class App{
    public static void main(String[] args) throws IOException {
        int place=0;
        int CharRead = 0;
        String input = "";

        Blockchain Vsolo = new Blockchain();

        System.out.println("Main Menu.\n 1- Enter data, 2- Look on all blockchain, 3- Look at custom block.");
        RawConsoleInput GC = new RawConsoleInput();
        for (;;) {
            CharRead = GC.read(false);

            //Move to menu and exit processing on esc and q
            if (CharRead == 27 || CharRead == 113){
                System.out.println("Main Menu.\n 1- Enter data, 2- Look on all blockchain, 3- Look at custom block.");
                place=0;
            }else if (CharRead == 120) {
                System.exit(0); //x is exit
            }

            //Realtime keys processing
            if (place == 1){
                if (CharRead != -2 && CharRead != 10 && CharRead != 13) {
                    input += (char)CharRead;
                    System.out.print((char)CharRead);
                }

                //10 for Intelij IDE, 13 for Windows
                if (CharRead == 10 || CharRead == 13){
                    System.out.println(input);
                    Vsolo.write(input);
                    input="";
                }
            }

            //Just show blockchain and nothing
            if (place == 2){
                //No key processing required, as it shows blockchain just once
            }

            //Enter number and show block
            if (place == 3){
                if (CharRead != -2 && CharRead != 10 && CharRead != 13) {
                    input += (char)CharRead;
                    System.out.print((char)CharRead);
                }

                //10 for Intelij IDE, 13 for Windows
                if (CharRead == 10 || CharRead == 13){
                    if (input != "") {
                        System.out.println("\n" + Vsolo.readBlock(Integer.parseInt(input)));
                        input = "";
                    }
                }
            }

            //Check blockchain on vlidity
            if (place == 4){
                //No key processing required, as it shows blockchain just once
            }


            //switch to other mods processing -> to_function switch
            if (place == 0){
                if (CharRead == 49){
                    System.out.println("Please enter data for saving in Vsolo blockchain.");
                    place=1;
                }

                if (CharRead == 50){
                    System.out.println("Vsolo blockchain:");
                    System.out.println(Vsolo.readBlockchain());
                    place=2;
                }

                if (CharRead == 51){
                    System.out.println("Block viewer: please type number of block");
                    place=3;
                }

                if (CharRead == 52){
                    System.out.println("4 mode.");
                    place=4;
                }

            }






            try
            {
                Thread.sleep(10);
            }
            catch(InterruptedException ex)
            {
                Thread.currentThread().interrupt();
            }
        }
        }
}

class RawConsoleInput {

    private static final boolean           isWindows     = System.getProperty("os.name").startsWith("Windows");
    private static final int               invalidKey    = 0xFFFE;
    private static final String            invalidKeyStr = String.valueOf((char)invalidKey);

    private static boolean                 initDone;
    private static boolean                 stdinIsConsole;
    private static boolean                 consoleModeAltered;

    /**
     * Reads a character from the console without echo.
     *
     * @param wait
     *   <code>true</code> to wait until an input character is available,
     *   <code>false</code> to return immediately if no character is available.
     * @return
     *   -2 if <code>wait</code> is <code>false</code> and no character is available.
     *   -1 on EOF.
     *   Otherwise an Unicode character code within the range 0 to 0xFFFF.
     */
    public static int read (boolean wait) throws IOException {
        if (isWindows) {
            return readWindows(wait); }
        else {
            return readUnix(wait); }}

    /**
     * Resets console mode to normal line mode with echo.
     *
     * <p>On Windows this method re-enables Ctrl-C processing.
     *
     * <p>On Unix this method switches the console back to echo mode.
     * read() leaves the console in non-echo mode.
     */
    public static void resetConsoleMode() throws IOException {
        if (isWindows) {
            resetConsoleModeWindows(); }
        else {
            resetConsoleModeUnix(); }}

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook( new Thread() {
            public void run() {
                shutdownHook(); }}); }

    private static void shutdownHook() {
        try {
            resetConsoleMode(); }
        catch (Exception e) {}}

//--- Windows ------------------------------------------------------------------

// The Windows version uses _kbhit() and _getwch() from msvcrt.dll.

    private static Msvcrt        msvcrt;
    private static Kernel32      kernel32;
    private static Pointer       consoleHandle;
    private static int           originalConsoleMode;

    private static int readWindows (boolean wait) throws IOException {
        initWindows();
        if (!stdinIsConsole) {
            int c = msvcrt.getwchar();
            if (c == 0xFFFF) {
                c = -1; }
            return c; }
        consoleModeAltered = true;
        setConsoleMode(consoleHandle, originalConsoleMode & ~Kernel32Defs.ENABLE_PROCESSED_INPUT);
        // ENABLE_PROCESSED_INPUT must remain off to prevent Ctrl-C from beeing processed by the system
        // while the program is not within getwch().
        if (!wait && msvcrt._kbhit() == 0) {
            return -2; }                                         // no key available
        return getwch(); }

    private static int getwch() {
        int c = msvcrt._getwch();
        if (c == 0 || c == 0xE0) {                              // Function key or arrow key
            c = msvcrt._getwch();
            if (c >= 0 && c <= 0x18FF) {
                return 0xE000 + c; }                              // construct key code in private Unicode range
            return invalidKey; }
        if (c < 0 || c > 0xFFFF) {
            return invalidKey; }
        return c; }                                             // normal key

    private static synchronized void initWindows() throws IOException {
        if (initDone) {
            return; }
        msvcrt = (Msvcrt)Native.loadLibrary("msvcrt", Msvcrt.class);
        kernel32 = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class);
        try {
            consoleHandle = getStdInputHandle();
            originalConsoleMode = getConsoleMode(consoleHandle);
            stdinIsConsole = true; }
        catch (IOException e) {
            stdinIsConsole = false; }
        if (stdinIsConsole) {
            registerShutdownHook(); }
        initDone = true; }

    private static Pointer getStdInputHandle() throws IOException {
        Pointer handle = kernel32.GetStdHandle(Kernel32Defs.STD_INPUT_HANDLE);

        return handle; }

    private static int getConsoleMode (Pointer handle) throws IOException {
        IntByReference mode = new IntByReference();
        int rc = kernel32.GetConsoleMode(handle, mode);
        if (rc == 0) {
            throw new IOException("GetConsoleMode() failed."); }
        return mode.getValue(); }

    private static void setConsoleMode (Pointer handle, int mode) throws IOException {
        int rc = kernel32.SetConsoleMode(handle, mode);
        if (rc == 0) {
            throw new IOException("SetConsoleMode() failed."); }}

    private static void resetConsoleModeWindows() throws IOException {
        if (!initDone || !stdinIsConsole || !consoleModeAltered) {
            return; }
        setConsoleMode(consoleHandle, originalConsoleMode);
        consoleModeAltered = false; }

    private static interface Msvcrt extends Library {
        int _kbhit();
        int _getwch();
        int getwchar(); }

    private static class Kernel32Defs {
        static final int  STD_INPUT_HANDLE       = -10;
        static final long INVALID_HANDLE_VALUE   = -1;
        static final int  ENABLE_PROCESSED_INPUT = 0x0001;
        static final int  ENABLE_LINE_INPUT      = 0x0002;
        static final int  ENABLE_ECHO_INPUT      = 0x0004;
        static final int  ENABLE_WINDOW_INPUT    = 0x0008; }

    private static interface Kernel32 extends Library {
        int GetConsoleMode (Pointer hConsoleHandle, IntByReference lpMode);
        int SetConsoleMode (Pointer hConsoleHandle, int dwMode);
        Pointer GetStdHandle (int nStdHandle); }

//--- Unix ---------------------------------------------------------------------

// The Unix version uses tcsetattr() to switch the console to non-canonical mode,
// System.in.available() to check whether data is available and System.in.read()
// to read bytes from the console.
// A CharsetDecoder is used to convert bytes to characters.

    private static final int               stdinFd = 0;
    private static Libc                    libc;
    private static CharsetDecoder          charsetDecoder;
    private static Termios                 originalTermios;
    private static Termios                 rawTermios;
    private static Termios                 intermediateTermios;

    private static int readUnix (boolean wait) throws IOException {
        initUnix();
        if (!stdinIsConsole) {                                  // STDIN is not a console
            return readSingleCharFromByteStream(System.in); }
        consoleModeAltered = true;
        setTerminalAttrs(stdinFd, rawTermios);                  // switch off canonical mode, echo and signals
        try {
            if (!wait && System.in.available() == 0) {
                return -2; }                                      // no input available
            return readSingleCharFromByteStream(System.in); }
        finally {
            setTerminalAttrs(stdinFd, intermediateTermios); }}   // reset some console attributes

    private static Termios getTerminalAttrs (int fd) throws IOException {
        Termios termios = new Termios();

            int rc = libc.tcgetattr(fd, termios);
             return termios;
             }

    private static void setTerminalAttrs (int fd, Termios termios) throws IOException {

            int rc = libc.tcsetattr(fd, LibcDefs.TCSANOW, termios);
            }

    private static int readSingleCharFromByteStream (InputStream inputStream) throws IOException {
        byte[] inBuf = new byte[4];
        int    inLen = 0;
        while (true) {
            if (inLen >= inBuf.length) {                         // input buffer overflow
                return invalidKey; }
            int b = inputStream.read();                          // read next byte
            if (b == -1) {                                       // EOF
                return -1; }
            inBuf[inLen++] = (byte)b;
            int c = decodeCharFromBytes(inBuf, inLen);
            if (c != -1) {
                return c; }}}

    // (This method is synchronized because the charsetDecoder must only be used by a single thread at once.)
    private static synchronized int decodeCharFromBytes (byte[] inBytes, int inLen) {
        charsetDecoder.reset();
        charsetDecoder.onMalformedInput(CodingErrorAction.REPLACE);
        charsetDecoder.replaceWith(invalidKeyStr);
        ByteBuffer in = ByteBuffer.wrap(inBytes, 0, inLen);
        CharBuffer out = CharBuffer.allocate(1);
        charsetDecoder.decode(in, out, false);
        if (out.position() == 0) {
            return -1; }
        return out.get(0); }

    private static synchronized void initUnix() throws IOException {
        if (initDone) {
            return; }
        libc = (Libc)Native.loadLibrary("c", Libc.class);
        stdinIsConsole = libc.isatty(stdinFd) == 1;
        charsetDecoder = Charset.defaultCharset().newDecoder();
        if (stdinIsConsole) {
            originalTermios = getTerminalAttrs(stdinFd);
            rawTermios = new Termios(originalTermios);
            rawTermios.c_lflag &= ~(LibcDefs.ICANON | LibcDefs.ECHO | LibcDefs.ECHONL | LibcDefs.ISIG);
            intermediateTermios = new Termios(rawTermios);
            intermediateTermios.c_lflag |= LibcDefs.ICANON;
            // Canonical mode can be switched off between the read() calls, but echo must remain disabled.
            registerShutdownHook(); }
        initDone = true; }

    private static void resetConsoleModeUnix() throws IOException {
        if (!initDone || !stdinIsConsole || !consoleModeAltered) {
            return; }
        setTerminalAttrs(stdinFd, originalTermios);
        consoleModeAltered = false; }

    protected static class Termios extends Structure {         // termios.h
        public int      c_iflag;
        public int      c_oflag;
        public int      c_cflag;
        public int      c_lflag;
        public byte     c_line;
        public byte[]   filler = new byte[64];                  // actual length is platform dependent
        @Override protected List<String> getFieldOrder() {
            return Arrays.asList("c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "filler"); }
        Termios() {}
        Termios (Termios t) {
            c_iflag = t.c_iflag;
            c_oflag = t.c_oflag;
            c_cflag = t.c_cflag;
            c_lflag = t.c_lflag;
            c_line  = t.c_line;
            filler  = t.filler.clone(); }}

    private static class LibcDefs {
        // termios.h
        static final int ISIG    = 0000001;
        static final int ICANON  = 0000002;
        static final int ECHO    = 0000010;
        static final int ECHONL  = 0000100;
        static final int TCSANOW = 0; }

    private static interface Libc extends Library {
        // termios.h
        int tcgetattr (int fd, Termios termios);
        int tcsetattr (int fd, int opt, Termios termios);
        // unistd.h
        int isatty (int fd); }

}