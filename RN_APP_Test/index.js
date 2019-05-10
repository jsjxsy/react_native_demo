import React from 'react';
import {Alert,DeviceEventEmitter, TouchableOpacity, Button, NativeModules, AppRegistry, StyleSheet, Text, View} from 'react-native';
// 下一句中的ToastExample即对应上文
// public String getName()中返回的字符串
//module.exports = NativeModules.ToastExample;

//import ToastExample from "./ToastExample";


class HelloWorld extends React.Component {

    componentWillMount() {
        DeviceEventEmitter.addListener('EventName', function  (msg) {
            console.log(msg);
            NativeModules.ToastExample.show("DeviceEventEmitter收到消息:" + "\n" + msg.key, NativeModules.ToastExample.SHORT)
        });
    }

    constructor(props) {
        super(props);
        this.state = {
            data: 'no_data',
        }
    }

    render() {
        return (
            <View style={styles.container}>
                <Text style={styles.hello}>Hello, World</Text>
                <TouchableOpacity onPress={this._onPressButton2.bind(this)}>
                    <Text style={styles.hello}>testAndroidCallbackMethod</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={this._onPressButton3.bind(this)}>
                    <Text style={styles.hello}>textAndroidPromiseMethod</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={this._onPressButton4.bind(this)}>
                    <Text style={styles.hello}>textAndroidSendEventMethod</Text>
                </TouchableOpacity>
                <Button
                    onPress={() => {
                        Alert.alert("你点击了按钮！");
                    }}
                    title="alert 点我！"
                />
                <Button
                    onPress={() => {
                        NativeModules.ToastExample.show("Awesome", NativeModules.ToastExample.SHORT);
                    }}
                    title="toast 点我！"
                />

                <Button
                    onPress={() => {
                        NativeModules.ImagePickerModule.pickImage().then((result) => {
                            this.setState({data: result});
                        }).catch((error) => {
                            this.setState({data: 'error'});
                        })
                    }}
                    title="image pick up"
                />

                <Text>result:{this.state.data}</Text>
            </View>
        );
    }

    _onPressButton2() {
        NativeModules.ToastExample.testAndroidCallbackMethod("HelloJack", (result) => {
            console.log("===>" + result);
            this.setState({data: result});
        });
    }

    _onPressButton3() {
        NativeModules.ToastExample.textAndroidPromiseMethod("abcx").then((result) => {
            this.setState({data: result});
        }).catch((error) => {
            this.setState({data: error});
        })
    }

    _onPressButton4(){
        NativeModules.ToastExample.sendEvent();
    }
}

var styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
    },
    hello: {
        fontSize: 20,
        textAlign: 'center',
        margin: 10,
    },
});

AppRegistry.registerComponent('wptNative', () => HelloWorld);