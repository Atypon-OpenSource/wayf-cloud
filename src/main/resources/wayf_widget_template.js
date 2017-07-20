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

const WAYF_BASE_URL = "https://wayf-cloud-sandbox.literatumonline.com";

const AUTHORIZATION_HEADER_KEY = "Authorization";
const AUTHORIZATION_HEADER_VALUE = "@PUBLISHER_JWT@";

const LOCAL_ID_COOKIE_NAME = "wayf-local";

function registerLocalId(localId) {
    var url = buildRegisterDeviceURL(localId);
    var request = new XMLHttpRequest();

    request.open("PATCH", url, true);
    request.setRequestHeader(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE);
    request.withCredentials = true;
    request.onreadystatechange = function() {
        if (request.readyState === XMLHttpRequest.DONE) {
            let event = new Event('wayf-done');
            document.dispatchEvent(event);
            if (request.status > 299) {
                console.log(request.status + " " + request.responseText);

                throw "Could not register local ID with WAYF";
            }
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

function readCookieValue(cookieName) {
    var cookieArray = document.cookie.split(';');

    for (var i = 0; i < cookieArray.length; i++) {
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

window.onload = function() {
    var localId = readLocalId();
    registerLocalId(localId);
}
