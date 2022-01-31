import React from 'react';
import { useState } from 'react';

const AddMachine = ({onAdd}) => {
    const [host, setHost] = useState('')
    const [port, setPort] = useState('')

    const onSubmit = async (e) => {
        e.preventDefault()

        if(!host || !port){
            alert('Please add a valid machine information')
            return
        }

        onAdd(host, port)
    }

    return (
        <form className='add-form' onSubmit={onSubmit}>
            <div className='form-control'>
                <label>Machine host</label>
                <input type='text' placeholder='Machine host' value={host} onChange={(e) => setHost(e.target.value)} />
            </div>
            <div className='form-control'>
                <label>Machine port</label>
                <input type='text' placeholder='Machine key' value={port} onChange={(e) => setPort(e.target.value)} />
            </div>
            <input type='submit' value="Add new machine" className='btn btn-block' />
        </form>
    )
};

export default AddMachine;
