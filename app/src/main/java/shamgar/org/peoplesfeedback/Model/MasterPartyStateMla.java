package shamgar.org.peoplesfeedback.Model;

import java.util.ArrayList;
import java.util.List;

public class MasterPartyStateMla {
    private ArrayList<PartyStateMla> partyStateMlas;

    public MasterPartyStateMla() {
    }

    public MasterPartyStateMla(ArrayList<PartyStateMla> partyStateMlas) {
        this.partyStateMlas = partyStateMlas;
    }

    public ArrayList<PartyStateMla> getPartyStateMlas() {
        return partyStateMlas;
    }

    public void setPartyStateMlas(ArrayList<PartyStateMla> partyStateMlas) {
        this.partyStateMlas = partyStateMlas;
    }
}
