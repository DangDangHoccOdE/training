class UserModel{
    id:number;
    firstName:string;
    lastName:string;
    address?:string;
    dateOfBirth:Date;
    job?:string;
    gender:string;
    avatar?:string;
    email:string;
    password:string;
    isActive:string;

    constructor(
        id:number,
        firstName:string,
        lastName:string,
        dateOfBirth:Date,
        gender:string,
        email:string,
        password:string,
        isActive:string,  
        address?:string,
        job?:string,
        avatar?:string
    ){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address= address;
        this.dateOfBirth = dateOfBirth;
        this.job = job;
        this.gender = gender;
        this.avatar = avatar;
        this.email = email;
        this.password = password;
        this.isActive = isActive;
    }
}

export default UserModel;