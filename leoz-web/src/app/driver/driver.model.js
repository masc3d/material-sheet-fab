var Driver = (function () {
    function Driver() {
    }
    Driver.prototype.toString = function () {
        return "firstname: " + this.firstname + " / surname: " + this.surname;
    };
    return Driver;
}());
export { Driver };
var Position = (function () {
    function Position() {
    }
    Position.prototype.toString = function () {
        return "lat: " + this.lat + "; lng: " + this.lng;
    };
    return Position;
}());
export { Position };
//# sourceMappingURL=driver.model.js.map