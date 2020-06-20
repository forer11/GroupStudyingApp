import React from 'react'; 
const axios = require("axios");
// import PropTypes from 'prop-types';


const randomNum = Math.random() * 100;
const color = randomNum > 50 ? 'green': 'red';

// /**
//  * Dynamic state which can be updated dynamically from everywhere
//  *  */ 
// class RandonNumberComponent extends React.Component{
//   state={
//     pageTitle: "dinamic title: Random Number Component"
//   };

//   render() {
//     return(
//       <h2 className="text-center" style={{color: color}}> 
//       <br></br>
//       {this.state.pageTitle}<br></br>

//       css object that gets the color as a variable. <br></br>
//       Remember that react has its own api so class=className here, etc...<br></br>
//       HELLO REACT with JSX (html like tags), and JS is enabled: {color.toString() + "because: " + randomNum.toString() }  
//       </h2>
//     );
//   }
// }



///////////////////////////////////////////////////////////////////////////////////////////////////////



// class RandonNumberComponent extends React.Component {
//   constructor(props) {
//     super(props);
//     this.state = {value: ''};

//     this.handleChange = this.handleChange.bind(this);
//     this.handleSubmit = this.handleSubmit.bind(this);
//   }

//   handleChange(event) {
//     this.setState({value: event.target.value});
//   }

//   handleSubmit(event) {
//     alert('A name was submitted: ' + this.state.value);
//     event.preventDefault();
//   }

//   render() {
//     return (
//       <form onSubmit={this.handleSubmit}>
//         <label>
//           Name:
//           <input type="text" value={this.state.value} onChange={this.handleChange} />
//         </label>
//         <input type="submit" value="Submit" />
//       </form>
//     );
//   }
// }



////////////////////////////////////////////////////////////////////////////////////////////////


class RandonNumberComponent extends React.Component {

  constructor(props) {
      super(props);
      this.state ={
          file: null
      };
      this.onFormSubmit = this.onFormSubmit.bind(this);
      this.onChange = this.onChange.bind(this);
  }
  onFormSubmit(e){
      e.preventDefault();
      const formData = new FormData();
      formData.append('photo',this.state.file);
      const config = {
          headers: {
              'content-type': 'multipart/form-data'
          }
      };
      axios.post("/upload",formData,config)
          .then((response) => {
              alert("The file is successfully uploaded");
          }).catch((error) => {
      });
  }
  onChange(e) {
      this.setState({file:e.target.files[0]});
  }

  render() {
      return (

<div className="container">
    <div className="card border-success mt-5">
        <h1 className="card-header">Backup New Recipe</h1>
        <div className="card-body">
            <div className="form-group">

          <form onSubmit={this.onFormSubmit}>
              <h3>File Upload</h3>
              <input type="file" name="photo" onChange= {this.onChange} />
              <button type="submit" class="btn mt-3">Upload</button>
          </form>

          </div>
        </div>
    </div>
</div>


      )
  }
}














  export default RandonNumberComponent;