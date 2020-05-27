# ordova-outsystems-multifileselector
File chooser plugin for Cordova.

Android supported.
iOS Support will be coming soon.

Install with Cordova CLI:

$ cordova plugin add https://github.com/vviek/cordova-outsystems-multifileselector.git

Supported Platforms:

* Android

## API

/**
	 * Displays native prompt for user to select a file.
	 *
	 * @returns Promise containing selected file's raw binary data,
	 * base64-encoded data, file size, display name.
	 *
	 * If user cancels, promise will be resolved as error.
	 * If error occurs, promise will be rejected.
	 */
	 window.plugins.multiplefileselect.selectfiles(
                    onSuccess, // For Success 
                    onError,    // For Error
                    IsCameraOpen // For Storage or Camera , For camera pass true else pass false
                  );

            function onSuccess(msg) {
               //Hangle Success
            }

            function onError(msg) {
                    //Hangle Error
                }

	
    
## Example Usage
 window.plugins.multiplefileselect.selectfiles(
                    onSuccess, // optional
                    onError,    // optional
                    false
                  );

            function onSuccess(msg) {
                alert(msg);
            }

            function onError(msg) {
                    alert(msg);
                }
