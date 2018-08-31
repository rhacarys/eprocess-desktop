/// Authentication setup ///
qz.security.setCertificatePromise(function(resolve, reject) {
	// Preferred method - from server
//	$.ajax("js/sign-message.js").then(resolve, reject);

	// Alternate method 1 - anonymous
	// resolve();

	// Alternate method 2 - direct
	resolve("-----BEGIN CERTIFICATE-----\n" +
			"MIID2DCCAsACCQDRTDxxxQwtGTANBgkqhkiG9w0BAQUFADCBrTELMAkGA1UEBhMC\n" +
			"QlIxCzAJBgNVBAgTAkFDMRMwEQYDVQQHEwpSaW8gQnJhbmNvMS0wKwYDVQQKEyRU\n" +
			"cmlidW5hbCBkZSBDb250YXMgZG8gRXN0YWRvIGRvIEFjcmUxEjAQBgNVBAsTCVBy\n" +
			"b3RvY29sbzESMBAGA1UEAxMJbG9jYWxob3N0MSUwIwYJKoZIhvcNAQkBFhZoZWxw\n" +
			"ZGVza0B0Y2UuYWMuZ292LmJyMB4XDTE3MDQwNTE0MzQzM1oXDTI3MDQwMzE0MzQz\n" +
			"M1owga0xCzAJBgNVBAYTAkJSMQswCQYDVQQIEwJBQzETMBEGA1UEBxMKUmlvIEJy\n" +
			"YW5jbzEtMCsGA1UEChMkVHJpYnVuYWwgZGUgQ29udGFzIGRvIEVzdGFkbyBkbyBB\n" +
			"Y3JlMRIwEAYDVQQLEwlQcm90b2NvbG8xEjAQBgNVBAMTCWxvY2FsaG9zdDElMCMG\n" +
			"CSqGSIb3DQEJARYWaGVscGRlc2tAdGNlLmFjLmdvdi5icjCCASIwDQYJKoZIhvcN\n" +
			"AQEBBQADggEPADCCAQoCggEBAMkAz/RyAWf4V7XZtc85czhJnxXh/dSnxsQMGFWH\n" +
			"IhZsCD3PyAhMOoQjrbNCfa7WOMZfuCjPTIo79QCu72PQ1SJKS1CKvCmgkdN0cGvR\n" +
			"VUhLVB2d8RQ3oaenMH3METk2WIZOwMlWfxEOco21hd7MqqM98i8oqJoJFldA8JYv\n" +
			"J2cRf3GHXRQGB/PvGMA//Md/TAhrtsK1Yx7+0NJgRGvaTyaQKoOrVu7x7rIUDkW1\n" +
			"9H6A0Sbc/BAAhxgducRFsuOhqiU/T/IaACUEtAbvvtLEAHZUrc0tdt8F6JJ0X161\n" +
			"jReKIcs3Yv5C10EA2IZC1trc/Tqv5Twj2A9i7XENV3hHEv0CAwEAATANBgkqhkiG\n" +
			"9w0BAQUFAAOCAQEAbs1xGYkI6JtUH58UtwLDDlRpHZsR8WBgf1Zv34gnc176qbmY\n" +
			"5mWefEYvYuI7WiblLi1CuYLyAH2xeJOYh0MR6TrePawRIIRf0RY8ZOijAgKOpiAP\n" +
			"NeVhCEEQ5ESJxvESkSyODs/RtbAyoQMiRA66XVY4gXAnD5ZVWB/4INUWxcwIXMng\n" +
			"q5WoiZdQgsw/RK0IoLcM1yidGH56j7xVyAefVU67ulQr9+NPxWyyyjNQ2Jewpypy\n" +
			"q86HdCOzyrQVCVTXmRRBrDq1GZ9Vl7UvfwRgP/FVpzORQuWj2TBFR3GmRVSh9uZF\n" +
			"yKOjyeRDjgliCxKXfUO0iGojdd79lHgw/uUgpQ==\n" +
			"-----END CERTIFICATE-----\n"
	);
});

/*qz.security.setSignaturePromise(function(toSign) {
	return function(resolve, reject) {
		// Preferred method - from server
		//$.ajax("js/sign-message?request=" + toSign).then(resolve,
		//reject);

		// Alternate method - unsigned
		resolve();
	};
});*/

function printProtocolo() {
	displayMessage("Imprimindo Protocolo: " + numero);
	launchQZ();
}


// / Connection ///
function launchQZ() {
	if (!qz.websocket.isActive()) {
		startConnection();
	} else {
		findPrinter(printer);
	}
}

function startConnection() {
	if (!qz.websocket.isActive()) {
		qz.websocket.connect().then(function() {
			findPrinter(printer);
		}).catch(handleConnectionError);
	} else {
		displayMessage('An active connection with QZ already exists.');
	}
}

function endConnection() {
	if (qz.websocket.isActive()) {
		qz.websocket.disconnect().then(function() {
			window.close();
		}).catch(handleConnectionError);
	} else {
		displayMessage('No active connection with QZ exists.');
	}
}

// / Detection ///
function findPrinter(query) {
	qz.printers.find(query).then(function(data) {
		displayMessage("Impressora Encontrada: " + data);
		setPrinter(data);

		printHTML();
		//printEtiqueta();
	}).catch(function(err) {
		msg = "Não é possível imprimir a etiqueta. verifique se a impressora " 
			+ printer 
			+ " encontra-se instalada neste computador";
		displayError(msg);
	});
}

function findPrinters() {
    qz.printers.find().then(function(data) {
        var list = '';
        for(var i = 0; i < data.length; i++) {
            list += " - " + data[i] + "\n";
        }

        displayMessage("Available printers:\n" + list);
    }).catch(displayError);
}

// / Pixel Printers ///
function printHTML() {
	var config = getUpdatedConfig();
	config.sifnature = true;

	var printData = [
		{
			type: 'html',
			format: 'plain',
			data: '<html>' +
			'   <div>' +
			'       <span>Protocolo: ' + numero + '</span><br/>' +
			'       <span>Data: ' + data + '</span><br/>' +
			'       <span>Hora: ' + hora + '</span><br/>' +
			'   </div>' +
			'</html>'
		}
		];

	qz.print(config, printData).then(function() {
		endConnection();
	}).catch(displayError);
}

qz.websocket.setClosedCallbacks(function(evt) {
	if (evt.reason) {
		displayMessage("<strong>Connection closed:</strong> " + evt.reason);
	}
});

qz.websocket.setErrorCallbacks(handleConnectionError);

var qzVersion = 0;
function findVersion() {
	qz.api.getVersion().then(function(data) {
		qzVersion = data;
	}).catch(displayError);
}


// / Helpers ///
function handleConnectionError(err) {
	if (err.target != undefined) {
		if (err.target.readyState >= 2) { // if CLOSING or CLOSED
			displayError("Connection to QZ Tray was closed");
		} else {
			displayError("A connection error occurred, check log for details");
		}
	} else {
		displayError(err);
	}
}

function displayError(err) {
	$("#output").append("<i style='color: red'>" + err + " (atualize a página para tentar novamente)</i><br/>");
}

function displayMessage(msg) {
	$("#output").append(msg + "<br/>");
}

// / QZ Config ///
var cfg = null;
function getUpdatedConfig() {
	if (cfg == null) {
		cfg = qz.configs.create(null);
	}
	return cfg
}

function setPrinter(p) {
	var cf = getUpdatedConfig();
	cf.setPrinter(p);

	if (typeof p === 'object' && p.name == undefined) {
		var shown;
		if (p.file != undefined) {
			shown = "<em>FILE:</em> " + p.file;
		}
		if (p.host != undefined) {
			shown = "<em>HOST:</em> " + p.host + ":" + p.port;
		}
	} else {
		if (p.name != undefined) {
			p = p.name;
		}

		if (p == undefined) {
			p = 'NONE';
		}
	}
}


function printEtiqueta() {
	// Send characters/raw commands to qz using "append"
	// This example is for EPL.  Please adapt to your printer language
	// Hint:  Carriage Return = \r, New Line = \n, Escape Double Quotes= \"
	var printData = [
	'I8,A,001\n',            
	'Q201,024\n',
	'q831\n',
	'rN\n',
	'S2\n',
	'D9\n',
	'ZT\n',
	'JF\n',
	'O\n',
	'R22,0\n',
	'f100\n',
	'N\n',

	'A132,13,0,3,1,1,N,"TCE-ACRE"\n',
	'A536,13,0,3,1,1,N,"TCE-ACRE"\n',

	'A12,44,0,3,1,1,N,"Data:' + data + '"\n',
	'A415,44,0,3,1,1,N,"Data:' + data + '"\n',

	'A237,44,0,3,1,1,N,"' + hora + '"\n',
	'A640,44,0,3,1,1,N,"' + hora + '"\n',

	'A50,76,0,3,1,1,N,"Número do Protocolo"\n',
	'A456,75,0,3,1,1,N,"Número do Protocolo"\n',

	'B61,97,0,1,2,6,66,N,"'+ numero +'"\n',
	'B468,97,0,1,2,6,66,N,"'+ numero +'"\n',

	'A93,166,0,3,1,1,N,"'+ numero +'"\n',
	'A499,164,0,3,1,1,N,"'+ numero +'"\n',

	'P1\n',
	];

	var config = getUpdatedConfig();

	qz.print(config, printData).then(function() {
		endConnection();
	}).catch(displayError);
}