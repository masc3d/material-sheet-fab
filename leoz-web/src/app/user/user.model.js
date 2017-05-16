var User = (function () {
    function User() {
    }
    User.prototype.toString = function () {
        return "firstname: " + this.firstname + " / surname: " + this.surname;
    };
    return User;
}());
export { User };
var Position = (function () {
    function Position() {
    }
    Position.prototype.toString = function () {
        return "lat: " + this.lat + "; lng: " + this.lng;
    };
    return Position;
}());
export { Position };
//# sourceMappingURL=user.model.js.map
