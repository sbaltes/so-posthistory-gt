package de.unitrier.st.soposthistorygt.researchPracticumResults;

import de.unitrier.st.soposthistory.version.PostVersionList;

public class GroundTruth extends StaticPostVersionsLists{

    private static void setPerfectLinks(PostVersionList postVersionList){
        for(int i=0; i<postVersionList.size(); i++){
            for(int j=0; j<postVersionList.get(i).getTextBlocks().size(); j++){

                if(i < postVersionList.size()-1) {
                    postVersionList.get(i).getTextBlocks().get(j).incrementSuccCount();
                }
                if(i > 0)
                    postVersionList.get(i).getTextBlocks().get(j).setPred(postVersionList.get(i-1).getTextBlocks().get(j), -1);

            }

            for(int j=0; j<postVersionList.get(i).getCodeBlocks().size(); j++){

                if(i < postVersionList.size()-1) {
                    postVersionList.get(i).getCodeBlocks().get(j).incrementSuccCount();
                }

                if(i > 0)
                    postVersionList.get(i).getCodeBlocks().get(j).setPred(postVersionList.get(i-1).getCodeBlocks().get(j), -1);

            }
        }
    }

    private void setPerfectLinks_3145655(){
        p_3145655.get(0).getTextBlocks().get(0).incrementSuccCount();
        p_3145655.get(0).getTextBlocks().get(1).incrementSuccCount();

        p_3145655.get(0).getCodeBlocks().get(0).incrementSuccCount();
        p_3145655.get(0).getCodeBlocks().get(1).incrementSuccCount();

        p_3145655.get(1).getTextBlocks().get(0).setPred(p_3145655.get(0).getTextBlocks().get(0), -1);
        p_3145655.get(1).getTextBlocks().get(1).setPred(p_3145655.get(0).getTextBlocks().get(1), -1);

        p_3145655.get(1).getCodeBlocks().get(0).setPred(p_3145655.get(0).getCodeBlocks().get(0), -1);
        p_3145655.get(1).getCodeBlocks().get(1).setPred(p_3145655.get(0).getCodeBlocks().get(1), -1);

        for(int i=1; i<p_3145655.size(); i++){
            for(int j=0; j<p_3145655.get(i).getTextBlocks().size(); j++){

                if(i < p_3145655.size()-1) {
                    p_3145655.get(i).getTextBlocks().get(j).incrementSuccCount();
                }

                if(i > 1)
                    p_3145655.get(i).getTextBlocks().get(j).setPred(p_3145655.get(i-1).getTextBlocks().get(j), -1);

            }

            for(int j=0; j<p_3145655.get(i).getCodeBlocks().size(); j++){

                if(i < p_3145655.size()-1) {
                    p_3145655.get(i).getCodeBlocks().get(j).incrementSuccCount();
                }

                if(i > 1)
                    p_3145655.get(i).getCodeBlocks().get(j).setPred(p_3145655.get(i-1).getCodeBlocks().get(j), -1);

            }
        }
    }

    private void setPerfectLinks_3758880(){
        p_3758880.get(0).getTextBlocks().get(0).incrementSuccCount();
        p_3758880.get(0).getCodeBlocks().get(0).incrementSuccCount();
        p_3758880.get(0).getCodeBlocks().get(2).incrementSuccCount();

        p_3758880.get(1).getTextBlocks().get(0).setPred(p_3758880.get(0).getTextBlocks().get(0), -1);

        p_3758880.get(1).getCodeBlocks().get(0).setPred(p_3758880.get(0).getCodeBlocks().get(0), -1);
        p_3758880.get(1).getCodeBlocks().get(1).setPred(p_3758880.get(0).getCodeBlocks().get(2), -1);


        for(int i=1; i<p_3758880.size(); i++){
            for(int j=0; j<p_3758880.get(i).getTextBlocks().size(); j++){

                if(i < p_3758880.size()-1) {
                    p_3758880.get(i).getTextBlocks().get(j).incrementSuccCount();
                }

                if(i > 1)
                    try {
                        p_3758880.get(i).getTextBlocks().get(j).setPred(p_3758880.get(i - 1).getTextBlocks().get(j), -1);
                    }catch(Exception e){
                        // Letzter Text im letzten Textblock in Version 11 wird hier nicht mehr gesetzt
                    }

            }

            for(int j=0; j<p_3758880.get(i).getCodeBlocks().size(); j++){

                if(i < p_3758880.size()-1) {
                    p_3758880.get(i).getCodeBlocks().get(j).incrementSuccCount();
                }

                if(i > 1)
                    p_3758880.get(i).getCodeBlocks().get(j).setPred(p_3758880.get(i-1).getCodeBlocks().get(j), -1);

            }
        }
    }

    private void setPerfectLinks_326440(){

        p_326440.get(1).getTextBlocks().get(0).setPred(p_326440.get(0).getTextBlocks().get(0), -1);
        p_326440.get(1).getCodeBlocks().get(0).setPred(p_326440.get(0).getCodeBlocks().get(0), -1);

        p_326440.get(2).getTextBlocks().get(0).setPred(p_326440.get(1).getTextBlocks().get(0), -1);
        p_326440.get(2).getCodeBlocks().get(0).setPred(p_326440.get(1).getCodeBlocks().get(0), -1);

        p_326440.get(3).getTextBlocks().get(0).setPred(p_326440.get(2).getTextBlocks().get(0), -1);
        p_326440.get(3).getCodeBlocks().get(0).setPred(p_326440.get(2).getCodeBlocks().get(0), -1);

        p_326440.get(4).getTextBlocks().get(0).setPred(p_326440.get(3).getTextBlocks().get(0), -1);
        p_326440.get(4).getCodeBlocks().get(0).setPred(p_326440.get(3).getCodeBlocks().get(0), -1);

        p_326440.get(5).getCodeBlocks().get(0).setPred(p_326440.get(4).getCodeBlocks().get(0), -1);
        p_326440.get(5).getCodeBlocks().get(1).setPred(p_326440.get(4).getCodeBlocks().get(1), -1);
        //p_326440.get(5).getTextBlocks().get(2).setPred(p_326440.get(4).getTextBlocks().get(2), -1);

        p_326440.get(6).getTextBlocks().get(0).setPred(p_326440.get(5).getTextBlocks().get(0), -1);
        p_326440.get(6).getTextBlocks().get(1).setPred(p_326440.get(5).getTextBlocks().get(1), -1);
        p_326440.get(6).getTextBlocks().get(2).setPred(p_326440.get(5).getTextBlocks().get(2), -1);
        p_326440.get(6).getTextBlocks().get(3).setPred(p_326440.get(5).getTextBlocks().get(3), -1);

        p_326440.get(6).getCodeBlocks().get(0).setPred(p_326440.get(5).getCodeBlocks().get(0), -1);
        p_326440.get(6).getCodeBlocks().get(1).setPred(p_326440.get(5).getCodeBlocks().get(1), -1);
        p_326440.get(6).getCodeBlocks().get(2).setPred(p_326440.get(5).getCodeBlocks().get(2), -1);

        p_326440.get(7).getTextBlocks().get(0).setPred(p_326440.get(6).getTextBlocks().get(0), -1);
        p_326440.get(7).getTextBlocks().get(2).setPred(p_326440.get(6).getTextBlocks().get(1), -1);
        p_326440.get(7).getTextBlocks().get(3).setPred(p_326440.get(6).getTextBlocks().get(2), -1);
        p_326440.get(7).getTextBlocks().get(4).setPred(p_326440.get(6).getTextBlocks().get(3), -1);

        p_326440.get(7).getCodeBlocks().get(0).setPred(p_326440.get(6).getCodeBlocks().get(0), -1);
        p_326440.get(7).getCodeBlocks().get(2).setPred(p_326440.get(6).getCodeBlocks().get(1), -1);
        p_326440.get(7).getCodeBlocks().get(3).setPred(p_326440.get(6).getCodeBlocks().get(2), -1);


        p_326440.get(8).getTextBlocks().get(0).setPred(p_326440.get(7).getTextBlocks().get(0), -1);
        p_326440.get(8).getTextBlocks().get(1).setPred(p_326440.get(7).getTextBlocks().get(1), -1);
        p_326440.get(8).getTextBlocks().get(2).setPred(p_326440.get(7).getTextBlocks().get(2), -1);
        p_326440.get(8).getTextBlocks().get(3).setPred(p_326440.get(7).getTextBlocks().get(3), -1);
        p_326440.get(8).getTextBlocks().get(4).setPred(p_326440.get(7).getTextBlocks().get(4), -1);

        p_326440.get(8).getCodeBlocks().get(0).setPred(p_326440.get(7).getCodeBlocks().get(0), -1);
        p_326440.get(8).getCodeBlocks().get(1).setPred(p_326440.get(7).getCodeBlocks().get(1), -1);
        p_326440.get(8).getCodeBlocks().get(2).setPred(p_326440.get(7).getCodeBlocks().get(2), -1);
        p_326440.get(8).getCodeBlocks().get(3).setPred(p_326440.get(7).getCodeBlocks().get(3), -1);

        for(int i=8; i<p_326440.size(); i++){
            for(int j=0; j<p_326440.get(i).getTextBlocks().size(); j++){

                if(i < p_326440.size()-1) {
                    p_326440.get(i).getTextBlocks().get(j).incrementSuccCount();
                }

                try {
                    p_326440.get(i).getTextBlocks().get(j).setPred(p_326440.get(i - 1).getTextBlocks().get(j), -1);
                }catch(Exception e){}

            }

            for(int j=0; j<p_326440.get(i).getCodeBlocks().size(); j++){

                if(i < p_326440.size()-1) {
                    p_326440.get(i).getCodeBlocks().get(j).incrementSuccCount();
                }

                p_326440.get(i).getCodeBlocks().get(j).setPred(p_326440.get(i-1).getCodeBlocks().get(j), -1);

            }
        }
    }

    private void setPerfectLinks_2581754(){

        p_2581754.get(0).getTextBlocks().get(0).incrementSuccCount();
        p_2581754.get(0).getTextBlocks().get(1).incrementSuccCount();

        p_2581754.get(0).getCodeBlocks().get(0).incrementSuccCount();
        p_2581754.get(0).getCodeBlocks().get(1).incrementSuccCount();


        p_2581754.get(1).getTextBlocks().get(0).setPred(p_2581754.get(0).getTextBlocks().get(0), -1);
        p_2581754.get(1).getTextBlocks().get(1).setPred(p_2581754.get(0).getTextBlocks().get(1), -1);

        p_2581754.get(1).getCodeBlocks().get(0).setPred(p_2581754.get(0).getCodeBlocks().get(0), -1);
        p_2581754.get(1).getCodeBlocks().get(1).setPred(p_2581754.get(0).getCodeBlocks().get(1), -1);

        p_2581754.get(1).getTextBlocks().get(0).incrementSuccCount();
        p_2581754.get(1).getTextBlocks().get(1).incrementSuccCount();

        p_2581754.get(1).getCodeBlocks().get(0).incrementSuccCount();
        p_2581754.get(1).getCodeBlocks().get(1).incrementSuccCount();


        p_2581754.get(2).getTextBlocks().get(0).setPred(p_2581754.get(1).getTextBlocks().get(0), -1);
        p_2581754.get(2).getTextBlocks().get(1).setPred(p_2581754.get(1).getTextBlocks().get(1), -1);

        p_2581754.get(2).getCodeBlocks().get(0).setPred(p_2581754.get(1).getCodeBlocks().get(0), -1);
        p_2581754.get(2).getCodeBlocks().get(1).setPred(p_2581754.get(1).getCodeBlocks().get(1), -1);

        p_2581754.get(2).getTextBlocks().get(0).incrementSuccCount();
        p_2581754.get(2).getTextBlocks().get(1).incrementSuccCount();
        p_2581754.get(2).getTextBlocks().get(2).incrementSuccCount();

        p_2581754.get(2).getCodeBlocks().get(0).incrementSuccCount();
        p_2581754.get(2).getCodeBlocks().get(1).incrementSuccCount();
        p_2581754.get(2).getCodeBlocks().get(2).incrementSuccCount();


        p_2581754.get(3).getTextBlocks().get(0).setPred(p_2581754.get(2).getTextBlocks().get(0), -1);
        p_2581754.get(3).getTextBlocks().get(1).setPred(p_2581754.get(2).getTextBlocks().get(1), -1);
        p_2581754.get(3).getTextBlocks().get(2).setPred(p_2581754.get(2).getTextBlocks().get(2), -1);

        p_2581754.get(3).getCodeBlocks().get(0).setPred(p_2581754.get(2).getCodeBlocks().get(0), -1);
        p_2581754.get(3).getCodeBlocks().get(1).setPred(p_2581754.get(2).getCodeBlocks().get(1), -1);
        p_2581754.get(3).getCodeBlocks().get(2).setPred(p_2581754.get(2).getCodeBlocks().get(2), -1);

        p_2581754.get(3).getTextBlocks().get(0).incrementSuccCount();
        p_2581754.get(3).getTextBlocks().get(1).incrementSuccCount();
        p_2581754.get(3).getTextBlocks().get(2).incrementSuccCount();
        p_2581754.get(3).getTextBlocks().get(3).incrementSuccCount();

        p_2581754.get(3).getCodeBlocks().get(0).incrementSuccCount();
        p_2581754.get(3).getCodeBlocks().get(1).incrementSuccCount();
        p_2581754.get(3).getCodeBlocks().get(2).incrementSuccCount();
        p_2581754.get(3).getCodeBlocks().get(3).incrementSuccCount();


        for(int i=4; i<p_2581754.size(); i++){
            for(int j=0; j<p_2581754.get(i).getTextBlocks().size(); j++){

                if (i < p_2581754.size() - 1){
                    p_2581754.get(i).getTextBlocks().get(j).incrementSuccCount();
                }

                p_2581754.get(i).getTextBlocks().get(j).setPred(p_2581754.get(i - 1).getTextBlocks().get(j), -1);

            }

            for(int j=0; j<p_2581754.get(i).getCodeBlocks().size(); j++){

                if(i <p_2581754.size()-1){
                    p_2581754.get(i).getCodeBlocks().get(j).incrementSuccCount();
                }

                p_2581754.get(i).getCodeBlocks().get(j).setPred(p_2581754.get(i-1).getCodeBlocks().get(j), -1);

            }
        }

    }

    public void setPerfectPredAndSucc(){

        setPerfectLinks(p_140861);
        setPerfectLinks(p_1109108);
        setPerfectLinks(p_5445161);
        setPerfectLinks(p_5599842);
        setPerfectLinks(p_9855338);
        setPerfectLinks(p_26196831);
        setPerfectLinks_3145655();
        setPerfectLinks_3758880();
        setPerfectLinks_326440();
        setPerfectLinks_2581754();
    }
}
