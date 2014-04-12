package iqq.app.util;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 *
 * 播放声音文件
 * 代码来自 http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
 * 并稍微做了修改
 *
 */

public class AePlayWave implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(AePlayWave.class);
    private File wavFile;
    private Position curPosition;
 
    private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb 
 
    enum Position { 
        LEFT, RIGHT, NORMAL
    };
 
    public AePlayWave(File wavFile) { 
    	this.wavFile = wavFile;
    	this.curPosition = Position.NORMAL;
    } 
 
    public AePlayWave(File wavFile, Position p) { 
        this.wavFile = wavFile;
        this.curPosition = p;
    } 
 
    public void run() { 
        AudioInputStream audioInputStream = null;
        try { 
            audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        } catch (Exception e) { 
            LOG.info("check wav file error!", e);
            return;
        } 
 
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
 
        try { 
            auline = (SourceDataLine) AudioSystem.getLine(info);
            auline.open(format);
        } catch (Exception e) { 
        	 LOG.info("open wav file error!", e);
        	 return;
        } 
 
        if (auline.isControlSupported(FloatControl.Type.PAN)) { 
            FloatControl pan = (FloatControl) auline
                    .getControl(FloatControl.Type.PAN);
            if (curPosition == Position.RIGHT) 
                pan.setValue(1.0f);
            else if (curPosition == Position.LEFT) 
                pan.setValue(-1.0f);
        } 
 
        auline.start();
        int nBytesRead = 0;
        byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
        try { 
            while (nBytesRead != -1) { 
                nBytesRead = audioInputStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) 
                    auline.write(abData, 0, nBytesRead);
            } 
        } catch (IOException e) { 
        	 LOG.info("play wav file error!", e);
        } finally { 
            auline.drain();
            auline.close();
        } 
    } 
} 