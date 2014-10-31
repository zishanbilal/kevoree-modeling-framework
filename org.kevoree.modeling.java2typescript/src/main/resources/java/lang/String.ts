
interface String {
    equals : (other:String) => boolean;
    startsWith : (other:String) => boolean;
    endsWith : (other:String) => boolean;
    length : () => number;
    matches :  (regEx:String) => boolean;
}

String.prototype.matches = function (regEx) {
    return this.match(regEx).length > 0;
};

String.prototype.equals = function (other) {
    return this == other;
};


String.prototype.startsWith = function (other) {
    for(var i = 0; i < other.length; i++) {
        if(other.charAt(i) != this.charAt(i)) {
            return false;
        }
    }
    return true;
};

String.prototype.endsWith = function (other) {
    for(var i = other.length-1; i >= 0; i--) {
        if(other.charAt(i) != this.charAt(i)) {
            return false;
        }
    }
    return true;
};