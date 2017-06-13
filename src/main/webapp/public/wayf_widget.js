/*
 * Copyright 2017 Atypon Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const WAYF_BASE_URL = "http://localhost:8080";

const AUTHORIZATION_HEADER_KEY = "Authorization";
const AUTHORIZATION_HEADER_VALUE = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwdWJsaXNoZXJDb2RlIjoiY29kZS1kNTA1YjFmYS1jZGQwLTQ4NzAtYTVjNy0yOGFjMjgzZDlkOTYifQ.vE7_h9xNLgJKBXDAh6yNRVt5XZK55QvxPvybXmQA_AU";

const GLOBAL_ID_HEADER_NAME = "X-Device-Id";

const LOCAL_ID_COOKIE_NAME = "localId";
const GLOBAL_ID_COOKIE_NAME = "deviceId";

function registerLocalId(localId) {
	var url = buildRegisterDeviceURL(localId);

	var request = new XMLHttpRequest();
	request.open("PATCH", url, true);
	request.setRequestHeader(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE);

	var globalId = readGlobalId();
	if (globalId) {
		request.setRequestHeader(GLOBAL_ID_HEADER_NAME, globalId);
	}

	request.onreadystatechange = function() {
	    if (request.readyState === XMLHttpRequest.DONE) {
	    	if (request.status > 299) {
	    		console.log(request.status + " " + request.responseText);

	    		throw "Could not register local ID with WAYF";
	    	}

	    	var globalId = request.getResponseHeader(GLOBAL_ID_HEADER_NAME);
	    	setGlobalId(globalId);
	    }
	}

	request.send(null);
}

function buildRegisterDeviceURL(localId) {
	const REGISTER_DEVICE_URL_PREFIX = "/1/device/";

	return WAYF_BASE_URL + REGISTER_DEVICE_URL_PREFIX + localId;
}

function readLocalId() {
    return readCookieValue(LOCAL_ID_COOKIE_NAME);
}

function setLocalId(localIdValue) {
	setCookieValue(LOCAL_ID_COOKIE_NAME + "=" + localIdValue);
}

function readGlobalId() {
	return readCookieValue(GLOBAL_ID_COOKIE_NAME);
}

function setGlobalId(globalId) {
	setCookieValue(GLOBAL_ID_COOKIE_NAME + "=" + globalId);
}

function readCookieValue(cookieName) {
    var cookieArray = document.cookie.split(';');

    for (var i=0; i < cookieArray.length; i++) {
        var cookie = cookieArray[i];

        // Trim leading whitespace
        while (cookie.charAt(0) == ' ') {
            cookie = cookie.substring(1, cookie.length);
        }

        var cookieNameEquals = cookieName + "=";

        if (cookie.indexOf(cookieNameEquals) == 0) {
            return cookie.substring(cookieNameEquals.length, cookie.length);
        }
    }

    return null;
}

function setCookieValue(value) {
	document.cookie = value;
}

window.onload = function() {
	// Test code, this should be done by the publisher
	setLocalId("local-id-publisher-a-85e53ff4-8701-4d4d-8d06-011668ce5365");

	var localId = readLocalId();
	registerLocalId(localId);
}