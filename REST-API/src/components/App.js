import React from 'react'; 
import ContentComp from './ContentComp';
import UploadImageComponent from './UploadImageComponent';



class App extends React.Component {
    // constructor(props) {
    //     super(props);
    //     this.state = {
    //         pageHeader: 'Naming Contests'
    //       };
    // }

  state = {
    var1: 'var1 content'
  };


  componentDidMount() {

  }

  componentWillUnmount() {

  }




  render() {
    return (
        <div>
            <div className="Header_Style">
                <UploadImageComponent message={this.props.myHomePageTitle}/>
            </div>
            <div className="Content_Style">
                <ContentComp var="CONTENT!"/>
            </div>
        </div>
    );
  }
}

export default App;









////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * 
 * stateless (static) component (so no need to define constructor/class)
 */
// const App = (props) => {
//     return (
//         <div>
//             <div className="Header_Style">
//                 <UploadImageComponent message={props.myHomePageTitle}/>
//             </div>
//             <div className="Content_Style">
//                 <ContentComp var="CONTENT!"/>
//             </div>
//         </div>
        
//     );
// };

// export default App;
