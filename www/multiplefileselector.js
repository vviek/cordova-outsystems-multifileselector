  function MultipleFileSelector() {
  }
  
  MultipleFileSelector.prototype.selectfiles = function (successCallback, errorCallback,isCamera) {

   cordova.exec(successCallback, errorCallback, "FileSelectorPlugin", "select", [isCamera]);
  };
  
  MultipleFileSelector.install = function () {
    if (!window.plugins) {
      window.plugins = {};
    }
  
    window.plugins.multiplefileselect = new MultipleFileSelector();
    return window.plugins.multiplefileselect;
  };
  
  cordova.addConstructor(MultipleFileSelector.install);
