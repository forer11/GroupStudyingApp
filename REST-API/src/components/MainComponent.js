import React from 'react'; 
import ContentComp from './ContentComp';
import RandonNumberComponent from './RandonNumberComponent';

/**
 * 
 * stateless (static) component 
 */
const MainComponent = (props) => {
    return (
        <div>
            <div className="Header_Style">
                <RandonNumberComponent message={props.myHomePageTitle}/>
            </div>
            <div className="Content_Style">
                <ContentComp var="CONTENT!"/>
            </div>
        </div>
        
    );
};

export default RandonNumberComponent;