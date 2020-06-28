import React from 'react';

class ContentComp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            pageHeader: 'Naming Contests'
          };
    }

//   state = {
//     pageHeader: 'Naming Contests'
//   };
  render() {
    return (
<div className="container">
    <div className="card border-success mt-5">
    <h1 className="card-header">Previous Recipes: {this.state.pageHeader}</h1>
        <div className="card-body">
            <div className="form-group">

          </div>
        </div>
    </div>
</div>
    );
  }
}

export default ContentComp;





// import React from 'react';
// // import PropTypes from 'prop-types';


// /**
//  * 
//  * stateless (static) component 
//  */
// const ContentComp = (props) => {
//     return (
//         <h4>
//         {props.var} content bla bla bla <br></br>
//         </h4>
//     );
// };
// // ContentComp.propTypes = {
// //     var: PropTypes.string
// //   };





//   export default ContentComp;





