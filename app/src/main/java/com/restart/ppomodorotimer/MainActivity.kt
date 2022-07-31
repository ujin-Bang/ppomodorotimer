package com.restart.ppomodorotimer

import android.media.SoundPool
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

    private var currentCountDownTimer: CountDownTimer? =
        null //이 카운트다운 타이머는 앱이시작하자마자 생기는 것이 아니므로 초기값 널로 설정.

    private val soundPool = SoundPool.Builder().build() //오디오파일을 재생하기 위해 사운드풀 사용.기본빌드값으로 객체화.

    private var tickingSoundId: Int? = null //사운드풀 로드시 아이디를 반환 -> 담을 변수 지정
    private var bellSoundId: Int? = null // 사운드풀 로드시 아이디를 반환 -> 담을 변수 지정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
        initSounds()
    }

    override fun onResume() { //화면에 다시 등장할 때
        super.onResume()

        soundPool.autoResume() //사운드 풀이 활성화된 모든스트림에 적용
    }

    override fun onPause() { //생명주기 화면에서 사라졌을때(바탕화면보기 버튼등)
        super.onPause()

        soundPool.autoPause() //사운드풀이 활성화된 모든스트림에 적용
    }

    override fun onDestroy() {
        super.onDestroy()

        soundPool.release() //앱이 종료되면 사운드풀도 끝낸다. 오디오,비디오 파일은 용량을 많이 차지하기 때문.
    }

    private fun bindView() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                //사용자가 실제로 건드렸을 때만 업데이트 해줌으로써 00초가 일정시간동안 유지되지 않게
                if (fromUser) {
                    updateRemainTime(progress * 60 * 1000L)

                }
            }

            //타이머를 새로 셋업하는 함수.
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //기존 타이머를 멈추게
                //기존 타이머를 멈추지 않은 상태로 새로운 시간을 지정하면 두가지 카운드다운이 동시에 이동하면서 실행됨.
                currentCountDownTimer?.cancel()
                currentCountDownTimer = null

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { //터치가 끝났을 때 카운트다운 시작하는 함수.

                seekBar
                    ?: return //시크바가 널일 경우 바로 리턴하므로 카운트다운을 진행하지 않게.  좌측에 있는 값이 널일 경우 우측의 값을 리턴한다. 코틀린에서는 익스프레션으로 리턴을 할 수 있기 때문에 온스타트 터치를 바로 리턴하도록.

                startCountDown()

            }

        })
    }

    private fun initSounds() {

        tickingSoundId = soundPool.load(this, R.raw.ticking, 1) //사운드풀 로드시 로드된 사운드의 아이디를 반환함.
        bellSoundId = soundPool.load(this, R.raw.bell, 1)

    }

    //CountDownTimer함수 만들기 => 반환타입(:CountDownTimer),return문 지우고 => = 를 사용하여 표현식으로 형식바꾼 문법사용.
    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L) { //1초간격으로 initialMillis시간만큼 실행
            override fun onTick(millisUntilFinished: Long) {

                updateRemainTime(millisUntilFinished) // 분, 초 텍스트뷰 갱신
                updateSeekBar(millisUntilFinished) //시크바 프로그래스값 갱신
            }

            //타이머 종료시 초기화해주기.
            override fun onFinish() {

                completeCountDown()
            }

        }

    private fun startCountDown(){

        //분(progress)를 60을 곱해 초로 만들고 다시 1000을 곱해 밀리세컨드로 만든다. => createCountDownTimer의 인자가 Long타입이므로 Long타입이 들어가야 하기 때문.
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)

        currentCountDownTimer?.start()

        tickingSoundId?.let { soundId ->

            soundPool.play(soundId, 1F, 1F, 0, -1, 1F)

        }
    }

    private fun completeCountDown() {
        updateRemainTime(0) //텍스트는 0000으로 초기화
        updateSeekBar(0) //시크바는 왼쪽으로 초기화

        soundPool.autoPause()

        bellSoundId?.let { soundId ->

            soundPool.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    //들어온 값으로 텍스트뷰에 표현할 분,초값 구하는 함수.
    private fun updateRemainTime(remainMillis: Long) {

        val remainMinutes = remainMillis / 1000 / 60  //입력받은 밀리세컨드를 초로 만들고 다시 60으로 나눠 분 추출
        val remainSeconds = remainMillis / 1000 % 60  //입력받은 밀리세턴드를 초로 만들고 60으로 나눈 나머지값 => 초 추출

        remainMinutesTextView.text = "%02d".format(remainMinutes) //분 추출
        remainSecondsTextView.text = "%02d".format(remainSeconds) //초 추출
    }

    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress =
            (remainMillis / 1000 / 60).toInt() //progress값은 Int타입이 들어와야 하기 때문에 toInt()로 형변환.
    }

}