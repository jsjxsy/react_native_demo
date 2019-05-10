#bash.sh
echo "==========>react-native bundle"
react-native bundle --platform android --dev false --entry-file index.js --bundle-output android/hello/wpt_rn/src/main/assets/index.android.bundle --sourcemap-output android/hello/wpt_rn/src/main/assets/index.android.map --assets-dest android/hello/app/src/main/res/
echo "=======>apk package"
cd android/hello
./gradlew assembleDebug
echo "========>apk install"
adb install -r app/build/outputs/apk/debug/app-debug.apk
echo "==========>apk start activity"
adb shell am start -a android.intent.action.MAIN -c android.intent.category.LAUNCHER -n demo.jinkegroup.com.hello/demo.jinkegroup.com.hello.MainActivity
echo "========>back"
cd ../../