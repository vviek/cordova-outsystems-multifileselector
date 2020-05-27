
  function MultipleFileSelector() {
  }
  
  MultipleFileSelector.prototype.selectfiles = function (successCallback, errorCallback) {
  
   cordova.exec(successCallback, errorCallback, "FileSelectorPlugin", "selectfiles", []);
  };
  
  MultipleFileSelector.install = function () {
    if (!window.plugins) {
      window.plugins = {};
    }
  
    window.plugins.multiplefileselect = new Notification();
    return window.plugins.multiplefileselect;
  };
  
  cordova.addConstructor(MultipleFileSelector.install);

  