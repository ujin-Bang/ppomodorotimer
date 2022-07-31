package com.restart.ppomodorotimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
    }

    private fun bindView(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               remainMinutesTextView.text = "%02d".format(progress) //정수형식 변환 => progress정수를 2자리로 표현.
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { //터치가 끝났을 때 카운트다운 시작하는 함수.

                seekBar ?: return //시크바가 널일 경우 바로 리턴하므로 카운트다운을 진행하지 않게.  좌측에 있는 값이 널일 경우 우측의 값을 리턴한다. 코틀린에서는 익스프레션으로 리턴을 할 수 있기 때문에 온스타트 터치를 바로 리턴하도록.

                //분(progress)를 60을 곱해 초로 만들고 다시 1000을 곱해 밀리세컨드로 만든다. => createCountDownTimer의 인자가 Long타입이므로 Long타입이 들어가야 하기 때문.
                createCountDownTimer(seekBar.progress * 60 * 1000L)
            }

        })
    }

    //CountDownTimer함수 만들기 => 반환타입(:CountDownTimer),return문 지우고 => = 를 사용하여 표현식으로 형식바꾼 문법사용.
    private fun createCountDownTimer(initialMillis: Long) =
        object: CountDownTimer(initialMillis, 1000L){ //1초간격으로 initialMillis시간만큼 실행
            override fun onTick(millisUntilFinished: Long) {

                updateRemainTime(millisUntilFinished) // 분, 초 텍스트뷰 갱신
                updateSeekBar(millisUntilFinished) //시크바 프로그래스값 갱신
            }

            //타이머 종료시 초기화해주기.
            override fun onFinish() {
                updateRemainTime(0) //텍스트는 0000으로 초기화
                updateSeekBar(0) //시크바는 왼쪽으로 초기화
            }

        }

    //들어온 값으로 텍스트뷰에 표현할 분,초값 구하는 함수.
    private fun updateRemainTime(remainMillis:Long) {

        val remainMinutes = remainMillis/ 1000 / 60  //입력받은 밀리세컨드를 초로 만들고 다시 60으로 나눠 분 추출
        val remainSeconds = remainMillis/ 1000 % 60  //입력받은 밀리세턴드를 초로 만들고 60으로 나눈 나머지값 => 초 추출

        remainMinutesTextView.text = "%02d".format(remainMinutes) //분 추출
        remainSecondsTextView.text = "%02d".format(remainSeconds) //초 추출
    }

    private fun updateSeekBar(remainMillis: Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt() //progress값은 Int타입이 들어와야 하기 때문에 toInt()로 형변환.
    }

}