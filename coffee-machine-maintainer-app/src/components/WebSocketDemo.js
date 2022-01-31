import React, { useState, useCallback, useEffect } from 'react';
import useWebSocket, { ReadyState } from 'react-use-websocket';
import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import Event from './Event'

export const WebSocketDemo = ({onDetail}) => {
    //Public API that will echo messages sent to it back to the client
    const [socketUrl, setSocketUrl] = useState('ws://localhost:8000');
    const [messageHistory, setMessageHistory] = useState([]);
    const [open, setOpen] = useState(false);
    const closeModal = () => setOpen(false);
    const {
        sendMessage,
        lastMessage,
        readyState,
    } = useWebSocket(socketUrl);

    useEffect(() => {
        if (lastMessage !== null) {
            //const json = JSON.parse(lastMessage.data.message)
            const msg = JSON.parse(lastMessage.data)
            msg.read = false
            msg.id = randomId() 
            console.log(msg)
            setMessageHistory(prev => prev.concat(msg));
            setOpen(true)
        }
    }, [lastMessage, setMessageHistory]);

    const notReadEvents = () => {
        return messageHistory.filter(msg => !msg.read)
    }

    const readAllEvents = () => {
        setMessageHistory(
            messageHistory.map(msg => true ? {...msg, read: true} : {})
            )
    }

    const randomId = () => {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
      }

    const handleClickSendMessage = useCallback(() =>
        sendMessage('Hello'), []);

    const connectionStatus = {
        [ReadyState.CONNECTING]: 'Connecting',
        [ReadyState.OPEN]: 'Open',
        [ReadyState.CLOSING]: 'Closing',
        [ReadyState.CLOSED]: 'Closed',
        [ReadyState.UNINSTANTIATED]: 'Uninstantiated',
    }[readyState];

    return (
        <div>
            <Popup open={open} closeOnDocumentClick onClose={closeModal}>
                <div className="modal">
                    <a className="close" onClick={closeModal}>
                        &times;
                    </a>
                    {notReadEvents().map((event) =>
                        <Event key={event.id} event={event} onDetail={() => {
                            onDetail(event.machineId);
                            closeModal();
                            readAllEvents();
                        }} />
                    )}
                </div>
            </Popup>
        </div>
    );
};