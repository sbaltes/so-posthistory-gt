package de.unitrier.st.soposthistorygt.util.anchorsURLs;

import java.util.Objects;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnchorTextAndUrlHandler {

    public Vector<AnchorTextAndUrlPair> extractAllAnchorsRefsAndURLpairs(String markdown){
        Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs = new Vector<>();

        for(int i=0; i< AnchorTextAndUrlPair.AnchorRefUrlType.values().length; i++){
            AnchorTextAndUrlPair.AnchorRefUrlType tmpType = AnchorTextAndUrlPair.AnchorRefUrlType.values()[i];
            Pattern tmpRegex = AnchorTextAndUrlPair.getRegexWithEnum(tmpType);

            assert tmpRegex != null;
            Matcher matcher = tmpRegex.matcher(markdown);
            while(matcher.find()){
                integrateMatchToAnchorTextAndUrlPairs(matcher, tmpType, anchorTextAndUrlPairs);
            }
        }

        System.out.println(anchorTextAndUrlPairs); // TODO: remove this
        return anchorTextAndUrlPairs;
    }

    private void integrateMatchToAnchorTextAndUrlPairs(Matcher matcher, AnchorTextAndUrlPair.AnchorRefUrlType type, Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs){

        switch (type){

            case type_anchorLink:
                anchorTextAndUrlPairs.add(
                        new AnchorTextAndUrlPair(
                                matcher.group(),
                                matcher.group(4),
                                null,
                                matcher.group(1),
                                matcher.group(3),
                                type
                        )
                );
                break;


            case type_markdownLinkBareTags:
                anchorTextAndUrlPairs.add(
                        new AnchorTextAndUrlPair(
                                matcher.group(),
                                null,
                                null,
                                matcher.group(1),
                                null,
                                type
                        ));
                break;


            case type_markdownLinkInline:
                anchorTextAndUrlPairs.add(
                        new AnchorTextAndUrlPair(
                                matcher.group(),
                                matcher.group(1),
                                null,
                                matcher.group(2),
                                matcher.group(3),
                                type
                        ));
                break;


            case type_markdownLinkReference:
                String anchor = null;
                String referenceTop = null;

                String referenceBottom = null;
                String url = null;
                String title = null;

                try {
                    anchor = matcher.group(7);
                    referenceTop = matcher.group(8);
                }catch (Exception ignored){}

                try{
                    referenceBottom = matcher.group(2);
                    url = matcher.group(3);
                    title = matcher.group(5);
                }catch (Exception ignored){}

                if(referenceTop == null){
                    boolean anchorTextAndURLPairAlreadyExisted = false;
                    for (AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs) {
                        if (anchorTextAndUrlPair.type == type && Objects.equals(referenceBottom, anchorTextAndUrlPair.reference)) {
                            anchorTextAndUrlPair.url = url;
                            anchorTextAndUrlPair.title = title;
                            anchorTextAndUrlPair.fullMatch2 = matcher.group();
                            anchorTextAndURLPairAlreadyExisted = true;
                            break;
                        }
                    }

                    if(!anchorTextAndURLPairAlreadyExisted) {
                        anchorTextAndUrlPairs.add(new AnchorTextAndUrlPair(
                                        null,
                                        null,
                                        referenceBottom,
                                        url,
                                        title,
                                        type
                                )
                        );
                        anchorTextAndUrlPairs.lastElement().fullMatch2 = matcher.group();
                    }

                }else{
                    boolean anchorTextAndURLPairAlreadyExisted = false;
                    for (AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs) {
                        if (anchorTextAndUrlPair.type == type && Objects.equals(referenceTop, anchorTextAndUrlPair.reference)) {
                            anchorTextAndUrlPair.anchor = anchor;
                            anchorTextAndUrlPair.fullMatch2 = matcher.group();
                            anchorTextAndURLPairAlreadyExisted = true;
                            break;
                        }
                    }

                    if(!anchorTextAndURLPairAlreadyExisted) {
                        anchorTextAndUrlPairs.add(new AnchorTextAndUrlPair(
                                        matcher.group(),
                                        anchor,
                                        referenceTop,
                                        null,
                                        null,
                                        type
                                )
                        );
                        anchorTextAndUrlPairs.lastElement().fullMatch = matcher.group();
                    }
                }

                break;


            case type_bareURL:

                for(AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs){
                    if(Objects.equals(anchorTextAndUrlPair.url, matcher.group(0))){
                        return;
                    }
                }
                anchorTextAndUrlPairs.add(
                        new AnchorTextAndUrlPair(
                                matcher.group(),
                                null,
                                null,
                                matcher.group(0),
                                null,
                                type
                        )
                );
                break;
        }
    }


    public String normalizeAnchorsRefsAndURLsForApp(String markdownText, Vector<AnchorTextAndUrlPair> anchorTextAndUrlPairs){

        for (AnchorTextAndUrlPair anchorTextAndUrlPair : anchorTextAndUrlPairs) {

            switch (anchorTextAndUrlPair.type){
                case type_markdownLinkInline:
                    // do nothing, this is the normalized form
                    break;

                case type_markdownLinkReference:

                    markdownText = markdownText.replace(
                            anchorTextAndUrlPair.fullMatch,
                            "[" + anchorTextAndUrlPair.anchor + "](" + anchorTextAndUrlPair.url + ((anchorTextAndUrlPair.title != null) ? " " + anchorTextAndUrlPair.title : "") + ")"
                    );

                    markdownText = markdownText.replace(
                            anchorTextAndUrlPair.fullMatch2,
                            ""
                    );

                    break;

                case type_anchorLink:
                    // do nothing, this would be the result after markup
                    break;

                case type_bareURL:
                    markdownText = markdownText.replaceFirst(anchorTextAndUrlPair.fullMatch, "<" + anchorTextAndUrlPair.url + ">");
                    break;

                case type_markdownLinkBareTags:
                    // do nothing, the url will be marked up by commonmark
                    break;
            }
        }

        return markdownText;
    }
}
