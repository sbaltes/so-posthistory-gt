package de.unitrier.st.soposthistorygt.tests;

import de.unitrier.st.soposthistorygt.researchPracticumResults.StaticPostVersionsLists;
import org.junit.jupiter.api.Test;

public class PostVersionListTest {

    @Test
    public void testGetStatisticsOfPostVersionList(){

        StaticPostVersionsLists staticPostVersionsLists = new StaticPostVersionsLists();
        staticPostVersionsLists.init();

        System.out.println(staticPostVersionsLists.getStatisticsOfPostVersionList());
    }
}
