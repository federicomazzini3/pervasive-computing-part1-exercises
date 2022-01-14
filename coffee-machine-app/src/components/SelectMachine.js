import React from 'react'
import { useState } from 'react'

const SelectMachine = ({onSelect}) => {
    const [address, setAddress] = useState('')

    const onSubmit = async (e) => {
        e.preventDefault()

        if(!address){
            alert('Please add a valid machine address')
            return
        }

        try {
            await fetch(`http://172.20.10.4:${address}/smart-coffee-machine`)
            onSelect(`http://172.20.10.4:${address}/smart-coffee-machine`, address)
        } catch(err) {
            alert('The machine seems not exists')
        }
    }

    return (
        <form className='add-form' onSubmit={onSubmit}>
            <div className='form-control'>
                <label>Machine address</label>
                <input type='text' placeholder='Machine key' value={address} onChange={(e) => setAddress(e.target.value)}/>
            </div>
            <input type='submit' value="Select a machine" className='btn btn-block'/>
        </form>
    )
}

export default SelectMachine
