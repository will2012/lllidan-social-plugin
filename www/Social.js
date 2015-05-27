
var argscheck = require('cordova/argscheck'),
    channel = require('cordova/channel'),
    utils = require('cordova/utils'),
    exec = require('cordova/exec'),
    cordova = require('cordova');

function Social() {
    
}


Social.prototype.authorize = function(successCallback, errorCallback, options) {
    argscheck.checkArgs('fFO', 'Social.authorize', arguments);
    options = options || {};
    var getValue = argscheck.getValue;
    var type = getValue(options.type, 0);
    var args = [type];

    exec(successCallback, errorCallback, "Social", "authorize", args);
};

module.exports = new Social();