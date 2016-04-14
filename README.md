# Round Soft Input

Round soft input mode for Android Wear

![DemoRound](demos/video_round.gif)
![DemoSquare](demos/video_square.gif)

## Features

* Configurable displayed characters 
* Support up to 26 keys on screen
* Customizable fling gestures for special functions, by default:
  * :arrow_left: backspace
  * :arrow_up: toggle case
  * :arrow_right: insert space
  * :arrow_down: cycle alternative keys
* Optimized for both right and left handed interaction
* Automatic detection of round or square screen

## Usage

### Basic

1. Start SoftInputActivity

  ```java
    Intent intent = new Intent(this, SoftInputActivity.class);
    startActivityForResult(intent, SoftInputActivity.TEXT_INSERT_REQUEST);
  ```

2. Then get the inserted text 'onActivityResult' method

  ```java
    if (requestCode == SoftInputActivity.TEXT_INSERT_REQUEST) {
  		if (resultCode == RESULT_OK) {
  			final String insertedText = data.getStringExtra(SoftInputActivity.INSERTED_TEXT_KEY_NAME);
  			//do awesome stuff...
  		}
  	}
  ```

### Customized

1. Pre insert text (useful for text edition)

  ```java
  	bundle.putString(SoftInputActivity.PRE_INSERTED_TEXT_KEY_NAME, mInputTextCustom.getText().toString());
  ```

2. Custom key characters

  ```java
  	intent.putExtra(SoftInputActivity.CUSTOM_CHAR_SET_KEY_NAME, "0123456789.");
  ```

3. Optimized for right handed use

  ```java
    bundle.putBoolean(SoftInputActivity.RIGHT_HANDED_LAYOUT_KEY_NAME, true);
  ```

Check out the sample with all the code  :wink:
  
## Coming up

* Better handle screen obscuration
* Word prediction/auto-correction
* Customize exit overlay and initial greet message
 

#### Trouble sleeping? Read all about this work ![here](http://vmota.bestporto.org/docs/paper_final_v1.pdf)  :grin:

