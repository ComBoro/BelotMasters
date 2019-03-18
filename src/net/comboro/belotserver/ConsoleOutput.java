package net.comboro.belotserver;

import javax.swing.*;
import java.io.*;

class ConsoleOutput extends OutputStream {

    private final PipedOutputStream out = new PipedOutputStream();
    private Reader reader;
    private JTextArea txtArea;

    public ConsoleOutput(JTextArea txtArea) throws IOException {
        this.txtArea = txtArea;
        PipedInputStream in = new PipedInputStream(out);
        reader = new InputStreamReader(in, "UTF-8");
    }

    @Override
    public void flush() throws IOException {
        if (reader.ready()) {
            char[] chars = new char[1024];
            int n = reader.read(chars);

            String txt = new String(chars, 0, n);
            txtArea.append(txt);
            if (!txtArea.hasFocus())
                txtArea.setCaretPosition(txtArea.getDocument().getLength());
        }
    }

    @Override
    public void write(byte[] bytes, int i, int i1) throws IOException {
        out.write(bytes, i, i1);
    }

    @Override
    public void write(int i) throws IOException {
        out.write(i);
    }
}