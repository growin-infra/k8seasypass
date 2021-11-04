$(document).ready(() => {
    changeDataPageAndSize();
    changeUserPageAndSize();
});

changeDataPageAndSize = () => {
    $('#dataPageSizeSelect').change(evt => {
        window.location.replace(`/data/list/?pageSize=${evt.target.value}&page=1`);
    });
}

changeUserPageAndSize = () => {
	$('#userPageSizeSelect').change(evt => {
		window.location.replace(`/user/list/?pageSize=${evt.target.value}&page=1`);
	});
}

function strToFormat(str) {
	var result;
	if (str.length == 14) {
		var date = new Date(str);
		result = date.getFullYear()+"년 ";
		result += date.getMonth()+1+"월 ";
		result += date.getDay()+"일 ";
		result += date.getHours()+"시 ";
		result += date.getMinutes()+"분 ";
		result += date.getSeconds()+"초"; 
	}
	return result;
}

/**
 * Date를 문자열로 변환한다.
 * @param value Date
 * @param outputFormat 형식 ex)YYYY.MM.DD
 */
var dateToString = function(value, outputFormat) {
    if (!value)
        return "";
 
    if(isNaN(value.getSeconds())||isNaN(value.getMinutes())
            ||isNaN(value.getHours())||isNaN(value.getDate())
            ||isNaN(value.getMonth())||isNaN(value.getFullYear()))return "";
     
    var sec = String(value.getSeconds());
    if (sec.length < 2)
        sec = "0" + sec;
     
    var min = String(value.getMinutes());
    if (min.length < 2)
        min = "0" + min;
     
    var hour = String(value.getHours());
    if (hour.length < 2)
        hour = "0" + hour;
     
    var date = String(value.getDate());
    if (date.length < 2)
        date = "0" + date;
 
    var month = String(value.getMonth() + 1);
    if (month.length < 2)
        month = "0" + month;
 
    var year = String(value.getFullYear());
    var output = "";
    var mask;
     
    var n = outputFormat != null ? outputFormat.length : 0;
    for (var i = 0; i < n; i++) {
        mask = outputFormat.charAt(i);
        if (mask == "s") {
            output += outputFormat.charAt(i + 1) == "/" && value.getSeconds() < 10 ? sec.substring(1) + "/" : sec;
            i++;
        } else if (mask == "m") {
            output += outputFormat.charAt(i + 1) == "/" && value.getMinutes() < 10 ? min.substring(1) + "/" : min;
            i++;
        } else if (mask == "h") {
            output += outputFormat.charAt(i + 1) == "/" && value.getHour() < 10 ? hour.substring(1) + "/" :  hour;
            i++;
        } else if (mask == "M") {
            output += outputFormat.charAt(i + 1) == "/" && value.getMonth() < 9 ? month.substring(1) + "/" : month;
            i++;
        } else if (mask == "D") {
            output += outputFormat.charAt(i + 1) == "/" && value.getDate() < 10 ? date.substring(1) + "/" : date;
            i++;
        } else if (mask == "Y") {
            if (outputFormat.charAt(i + 2) == "Y") {
                output += year;
                i += 3;
            } else {
                output += year.substring(2, 4);
                i++;
            }
        } else {
            output += mask;
        }
    }
    return output;
};


/**
 * 문자열을 Date로 변환.
 * @param valueString 문자열
 * @param inputFormat 형식 ex) YYYY-MM-DD hh:mm:ss
 */
var stringToDate = function(valueString, inputFormat) {
    if(!valueString){
        return valueString;
    }
    var mask;
    var temp;
    var dateString = "";
    var monthString = "";
    var yearString = "";
    var hourString = "";
    var miniteString = "";
    var secondString = "";
    var j = 0;
 
    var n = inputFormat.length;
    for (var i = 0; i < n; i++, j++) {
        temp = "" + valueString.charAt(j);
        mask = "" + inputFormat.charAt(i);
 
        if (mask == "M") {
            if (isNaN(Number(temp)) || temp == " ")
                j--;
            else
                monthString += temp;
        } else if (mask == "D") {
            if (isNaN(Number(temp)) || temp == " ")
                j--;
            else
                dateString += temp;
        } else if (mask == "Y") {
            yearString += temp;
        } else if (mask == "h") {
            hourString += temp;
        } else if (mask == "m") {
            miniteString += temp;
        } else if (mask == "s") {
            secondString += temp;
        } else if (!isNaN(Number(temp)) && temp != " ") {
            return null;
        }
    }
 
    temp = "" + valueString.charAt(inputFormat.length - i + j);
    if (!(temp == "") && (temp != " "))
        return null;
 
    var monthNum = Number(monthString);
    var dayNum = Number(dateString);
    var yearNum = Number(yearString);
    var hourNum = Number(hourString);
    var miniteNum = Number(miniteString);
    var secondNum = Number(secondString);
 
    if (isNaN(yearNum) || isNaN(monthNum) || isNaN(dayNum))
        return null;
 
    if (yearString.length == 2 && yearNum < 70)
        yearNum += 2000;
 
    var newDate = new Date(yearNum, monthNum - 1, dayNum);
    newDate.setHours(hourNum, miniteNum, secondNum);
 
    if (dayNum != newDate.getDate() || (monthNum - 1) != newDate.getMonth())
        return null;
 
    return newDate;
}

/**
 * 14자리 날짜를 년월일시분초로 변환
 */
var strToCustomFormat = function(v) {
	if (v.length != 14) return;
	return v.substr(0,4)+"년 "+v.substr(4,2)+"월 "+v.substr(6,2)+"일 "+v.substr(8,2)+"시 "+v.substr(10,2)+"분 "+v.substr(12,2)+"초";
}

/**
 * 초단위를 시분초로 변환
 */
var secToStr = function(seconds) {
	var result = "";
//	var pad = function(x) { return (x < 10) ? "0"+x : x; }
	var pad = function(x) { return x; }
	if ((parseInt(seconds / (60*60))) > 0) {
		result = pad(parseInt(seconds / (60*60))) + "시 ";
	}
	if ((parseInt(seconds / 60 % 60)) > 0) {
		result += pad(parseInt(seconds / 60 % 60)) + "분 ";
	}
	if ((seconds % 60) > 0) {
//		result += (Math.round(pad(seconds % 60)) < 10 ? "0"+Math.round(pad(seconds % 60)):Math.round(pad(seconds % 60))) + "초";
//		result += (Math.round(pad(seconds % 60))) + "초";
		result += ((pad(seconds % 60)).toFixed(0)) + "초";
	}
	if(seconds == "0"){
		result = "0초"
	}
	return result;

}


//숫자만 - 해당 Input 속성 삽입
//onkeypress="return fn_press(event, 'numbers');" onkeydown="fn_press_han(this);" style="ime-mode:disabled;"
function fn_press(event, type) {
	if(type == "numbers") {
	    if(event.keyCode < 48 || event.keyCode > 57) return false;
	    //onKeyDown일 경우 좌, 우, tab, backspace, delete키 허용 정의 필요
	}
}
//영어, 숫자만 - 해당 Input 속성 삽입
//onkeydown="fn_press_han(this);" style="ime-mode:disabled;"
function fn_press_han(obj) {
	//좌우 방향키, 백스페이스, 딜리트, 탭키에 대한 예외
	if (event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 46) {
		return;
	}
	obj.value = obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
}
//이메일 유효성 검사
function validateEmail(email) {
	  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	  return re.test(email);
}

function validateUrl(url){
	//url 유효성 검사
	let regex = /(http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
	
	//올바른 url이 맞다면 해당 url로 이동
//	if(regex.test(url)){
//		location.href = url;
//	}
	return regex.test(url); 
}
