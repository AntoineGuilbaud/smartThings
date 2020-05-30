/**
Copyright Sinopé Technologies
1.3.2
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
**/

definition(
    name: "Sinopé Technologies Inc. service manager",
    namespace: "Sinopé Technologies Inc.",
    author: "Sinopé Technologies Inc.",
    description: "Smart app to support Sinopé Miwi and Wi-Fi devices linked to Neviweb",
    category: "SmartThings Labs",
    iconUrl: "https://neviweb.com/assets/icons/icon-144x144.png",
    iconX2Url: "https://neviweb.com/assets/icons/icon-144x144.png"
    
)

preferences {
  	section ("Devices to connect") {
    	input "switches", "capability.switch", title: "Select your Sinopé Technologies Inc. devices", multiple: true, required: false
    	input "thermostats", "capability.thermostatHeatingSetpoint", title: "Select your Sinopé Technologies Inc. thermostat?", multiple: true, required: false
    }
    section ("Account info") {
        input "email", "text", title: "Your neviweb® account login e-mail", description: "Your neviweb® account login e-mail"
        input "password", "password", title: "Your neviweb® account login password", description: "Your neviweb® account login password"
    }
}

def refreshDevices(data){
    def connection = getSessionId();
    def output;
    if(connection.session){
        output = ["session": connection.session, "action": "refresh", "deviceId": ""]
        log.info("Refreshing devices")
    }else if(connection.error){
        output = [error: connection.error, deviceId: ""]
        log.warn("Refreshing devices failed");
    }else{
        output = ["error": "Smart app configuration problem", deviceId: ""]
        log.warn("Refreshing devices failed");
    }
    switches.each{device ->
            output?.deviceId = device.id;
            startDevicesCommunicationsHandlerRefresh(output);
    }
    thermostats.each{device ->
        output?.deviceId = device.id;
        startDevicesCommunicationsHandlerRefresh(output);
    }
    return ;
}

def uninstalled() {
    unsubscribe();
    unschedule();
    logout([sessionId: true, refreshToken: true]);
    return ;
}

def updated() {
    // log.info "Updating Service Manager"
	state.server="https://smartthings.neviweb.com/";
	unsubscribe();
    unschedule();
    logout([sessionId: true, refreshToken: true]);

    runEvery15Minutes(refreshDevices);

    state.lockedUntil = null;
    state.error = null;
    login();
    state.sessionTime = 0;

    subscribe(switches, "switch", startDevicesCommunicationsHandler);
    subscribe(switches, "level", startDevicesCommunicationsHandler);
    subscribe(thermostats, "thermostat", startDevicesCommunicationsHandler);
    return ;
}

def installed() {
    subscribe(switches, "switch", startDevicesCommunicationsHandler);
    subscribe(switches, "level", startDevicesCommunicationsHandler);
    subscribe(thermostats, "thermostat", startDevicesCommunicationsHandler);
    return ;
}

def startDevicesCommunicationsHandler(evt){
    def data = parseJson(evt.data);
    def device = getDeviceObj(data?.deviceId);
    if(data?.action == "resetSession"){
        if(!state?.error){
            logout([sessionId: true]);
        }
    }else if(device){
        def connection = getSessionId();
        if(connection?.session){
            data?.session = connection.session;
            device?.StartCommunicationWithServer(data);
        }else if(connection.error){
            device?.StartCommunicationWithServer([error: connection.error]);
        }else{
            device?.StartCommunicationWithServer([error: "Invalid Smart App configuration"]);
        }
    }
    return;
}

def startDevicesCommunicationsHandlerRefresh(data){
    if((data?.session && data?.deviceId) || (data?.error && data?.deviceId)){
        try{
            getDeviceObj(data.deviceId).StartCommunicationWithServer(data);
        }catch(e){
            // caused by non-sinope devices. Ignored
        }
    }
    return;
}

private getDeviceObj(deviceId){
    def deviceToReturn = null;
    switches.each{device ->
    	if(device?.id == deviceId) {
            deviceToReturn = device;
        }
    }
    thermostats.each{device ->
    	if(device?.id == (deviceId)) {
            deviceToReturn = device;
        }
    }
    if(deviceToReturn){
        return deviceToReturn;
    }else{
        // log.error "no device with id  "+ deviceId
        return null;
    }
}

def getSessionId(){
	if(state?.session && state?.sessionTime && 
    state.sessionTime < now() && 
    now() < state.sessionTime+595000 ){
		state.sessionTime = now();
		return [session: state.session];
	}
    else{
        if(state?.lockedUntil && (state?.lockedUntil <= now())){
            state?.lockedUntil = null;
            state?.error = null;
            if(state.session || state.refreshToken){
                logout([sessionId: true, refreshToken: true]);
            }
            return login();
        }else if(state?.lockedUntil){
            return [error: "${state?.error} ${(state.lockedUntil - now())/1000} seconds."]
        }else if(state?.error){
            return [error: state?.error]
        }else{
            return connect();
        }
    }
}

def login() {
    def params = [
        uri: "${state.server}",
        path: 'login',
        requestContentType: "application/json; charset=UTF-8",
        body: ["username": settings.email, "password": settings.password, "interface": "neviweb", "stayConnected": true]
    ]
    def connection = requestApi("login", params);
    if(connection?.error){
        log.warn("Failed to login");
    }
    return connection;
}

def connect() {
    
    def params = [
        uri: "${state.server}",
        path: 'connect',
        requestContentType: "application/json; charset=UTF-8",
        headers: [
            'refreshToken' : state?.refreshToken,
            'Session-Id' : state?.session] 
    ];
    def connection = requestApi("connect", params);
    if(connection?.error){
        log.warn("Failed to connect");
    }
    return connection;
}

def logout(data) {
    def params = [
		uri: "${state.server}",
        path: "logout",
       	requestContentType: "application/json, text/javascript, */*; q=0.01",
        headers: [
            'refreshToken' : (data.refreshToken?state?.refreshToken:null),
            'Session-Id' : (data.sessionId?state?.session:null)
        ]
   	];
    if(params.headers['refreshToken'] || params.headers['Session-Id'] ){
        requestApi("logout", params);
        if(data?.refreshToken){
            state?.refreshToken = null;
        }
        if(data?.sessionId){
            state?.session = null;
            state.sessionTime = 0;
        }
    }
    return ;
}

def requestApi(actionApi, params){
    params.uri = "https://smartthings.neviweb.com/"
	log.trace("api call - ${actionApi}");
	switch(actionApi){
		case "login":
			httpPost(params) { resp ->
			// // log.info(resp.data)
			if (!resp?.data?.session || resp?.data?.error){
                log.warn("Unable to login on neviweb");
                if(state.refreshToken){
                    logout([refreshToken: true, sessionId: true]);
                }
                if(resp?.data?.error?.code == "USRLOCKED" || resp?.data?.error?.code == "USRMAXLOGRETRY"){
                    state.lockedUntil = now()+(resp?.data?.error?.data?.time*1000)
                    state.error = "Max login retry exceeded. Your account is locked for ";
                    return [error: state.error +" ${(state.lockedUntil - now())/1000} seconds."];
                }else if(resp?.data?.error?.code == "ACCSESSEXC"){
                    state.lockedUntil = now()+(60*10*1000);
                    state.error = "You have too many open session on Neviweb. Next retry in in ";
                    return [error: state.error +" ${(state.lockedUntil - now())/1000} seconds."];
                }else if(resp?.data?.error?.code == "USRBADLOGIN"){
                    state.error = "Unable to connect to Neviweb, please verify your Sinopé Technologies Inc. service manager configuration. ";
                    return [error: state.error];
                }else{
                    state.lockedUntil = now()+(60*60*1000);//locked for the next hour for generic error
                    state.error = "Unable to connect to Neviweb, please verify your Sinopé Technologies Inc. service manager configuration. Next retry in ";
                    return [error: state.error+" ${(state.lockedUntil - now())/1000} seconds."];
                }
            }else{
                if(state.refreshToken && state.refreshToken!=resp.data.refreshToken){
                    logout([session:true, refreshToken: true]);
                }
				// log.info("New access token")
				state.session = resp.data.session;
				state.sessionTime = now();
				state.refreshToken = resp.data.refreshToken;
				// // log.info("Session : " + state.session)
                return [session: state.session];
			}
		}
		break;
		case "connect":
			httpPost(params) { resp ->
			// // log.info(resp.data)
			if (!resp?.data?.session || resp?.data?.error){
                if(state.session){
                    logout([sessionId: true]);
                }
                if(resp?.data?.error?.code == "USRLOCKED" || resp?.data?.error?.code == "USRMAXLOGRETRY"){
                    state.lockedUntil = now()+(resp?.data?.error?.data?.time*1000)
                    state.error = "Max login retry exceeded. Your account is locked for ";
                    return [error: state.error +" ${(state.lockedUntil - now())/1000} seconds."];
                }else if(resp?.data?.error?.code == "ACCSESSEXC"){
                    state.lockedUntil = now()+(60*10*1000);
                    state.error = "You have too many open session on Neviweb. Next retry in in ";
                    return [error: state.error +" ${(state.lockedUntil - now())/1000} seconds."];
                }else{
                    state.lockedUntil = now()+(60*60*1000);//locked for the next hour for generic error
                    state.error = "Unable to connect to Neviweb, please verify your Sinopé Technologies Inc. service manager configuration. Next retry in ";
                    return [error: state.error+" ${(state.lockedUntil - now())/1000} seconds."];
                }
			}else{
				// log.info("New access token")
                if(state.session && state.session!=resp.data.session){
                    logout([sessionId: true]);
                }
				state.session=resp.data.session;
				state.sessionTime = now();
				// // log.info("Session : " + state.session)
                return [session: state.session];
			}
		}
		break;
		case "logout":
			params.path = "logout";
			params.headers = ['Session-Id' : state?.session]
			httpGet(params) {resp ->
                return rsep?.data?.success;
			}
		break;
	}
}