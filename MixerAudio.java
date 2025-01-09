
/*Program: ICS Culminating 2022 - Geometry Dash
 * 
 * Created by: Peter Meijer
 *
 * Improved by: Miller Downey
 *       
 * For: Donald Corea
 * 
 * Class: Audio Player
 * 
 * This class is the audio player class. It handles the playing, looping, and stopping of audio clips
 * used for music and sound effects. An object of this class is created with a wav file string name inputed
 * the audio is then loaded and the instance methods can be called on the object to update the audio.
 */


import java.util.*;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class MixerAudio{

   public static ArrayList<Clip> audioClips = new ArrayList<Clip>();
   public static ArrayList<Integer> frameposition =  new ArrayList<Integer>();
   
   //constructor to create audioplayer based on file name
   public static Clip load(String fileName) 
   {
      Clip clip = null;
      try
      {
         File file = new File(fileName);
         if (file.exists())
         {
            AudioInputStream sound = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(sound);
            audioClips.add(clip);
            frameposition.add(0);
            
         }
      } 
      catch (Exception e){
         System.err.println("Could not load file " + fileName);
         e.printStackTrace();
      } 
      return clip; 
   }
       
   //method play to play the audio
   public static void play(Clip clip)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      
      if (c != null && !c.isRunning()){
         c.setFramePosition(0);
         c.start();   
      }   
   }
   public static void play(Clip clip, int count)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      
      if (!c.isRunning()){
         c.setFramePosition(0);
         c.start();   
      } 
      if (count > 1){
         loop(c,  count);
      }     
   }

   
   
   public static void rewind(Clip clip){
      Clip c = audioClips.get(audioClips.indexOf(clip));
      c.setFramePosition(0);
      c.start();
   }
   
   //method to set looping of audio to forever
   public static void loop(Clip clip)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      c.loop(Clip.LOOP_CONTINUOUSLY);
   }
   public static void loop(Clip clip, int count)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      c.loop(count);
   }
   
   
   //method to stop the playing of the clip
   public static void stop(Clip clip)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      c.stop();
   }
   
   
   //following methods added by Miller. D
   //method to get the frame location currently then stop
   public static void pause(Clip clip)
   {
      Clip c = audioClips.get(audioClips.indexOf(clip));
      frameposition.set(audioClips.indexOf(clip), c.getFramePosition());
      c.stop();
   }
   //method to resume from the frame location from the pause method
   public static void resume(Clip clip){
      Clip c = audioClips.get(audioClips.indexOf(clip));   
      c.setFramePosition(frameposition.get(audioClips.indexOf(clip)));
      c.start();
   }
   public static void remove(Clip clip){
      audioClips.remove(audioClips.indexOf(clip));   
   }
   
}

