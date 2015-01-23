package scaloid.example

import android.content.Context
import android.graphics.{Canvas, Color, Paint, Rect}
import android.media.{AudioManager, SoundPool}
import android.view.{MotionEvent, Window, WindowManager}
import org.scaloid.common._
import org.scaloid.util.Configuration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


sealed class Scene() {
  var r: Rect = new Rect(0, 0, 50, 50)
  var dx: Int = 1
  var dy: Int = 1
  var w: Int = 50
  var h: Int = 100


  def update(): Unit = {
    if (r.right >= w) dx = -1
    if (r.left <= 0) dx = +1
    if (r.bottom >= h) dy = -1
    if (r.top <= 0) dy = +1
    r.offset(dx, dy)
  }

}

sealed class TestView(scene: Scene)(implicit c: Context) extends SView with TagUtil {

  val whitePaint = new Paint()
  whitePaint.setColor(Color.WHITE)
  val sp = new SoundPool(4, AudioManager.STREAM_MUSIC, 0)
//  val sp = new Builder().setMaxStreams(4).setAudioAttributes(new AudioAttributes.Builder().setFlags(AudioAttributes.CONTENT_TYPE_MUSIC).build()).build()
  val click = sp.load(c, R.raw.click, 1)

  def play(snd: Int): Unit = sp.play(snd, 1, 1, 1, 0, 1)

  override def onDraw(canvas: Canvas): Unit = {
    canvas.drawRect(scene.r, whitePaint)
    invalidate()
  }

  onTouch { (_, evt) =>
    evt.getAction match {
      case MotionEvent.ACTION_DOWN =>
        info(s"${evt.getX} : ${evt.getY} : ${evt.toString}")
        play(click)
      case _ =>
    }
    true
  }
}


class HelloScaloid extends SActivity {

  var scene = new Scene()
  var running = true

  onCreate {
    getWindow.requestFeature(Window.FEATURE_NO_TITLE)
    getWindow.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    contentView = new TestView(scene)
    scene.w = width
    scene.h = height
    Future {
      while(running) {
        scene.update()

        Thread.sleep(10)
      }
    }
  }




}
