package pl.bergmann;

public class CustomTuple<X, Y> {
    private  X startingDate;
    private  Y endingDate;

    public CustomTuple(){}

    public CustomTuple(X startingDate, Y endingDate)
    {
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }


    public X getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(X startingDate) {
        this.startingDate = startingDate;
    }

    public Y getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Y endingDate) {
        this.endingDate = endingDate;
    }
}
