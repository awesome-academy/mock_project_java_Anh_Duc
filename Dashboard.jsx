import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const Dashboard = () => {
    const [userCount, setUserCount] = useState({
        totalUsers: 0,
        activeUsers: 0,
        inactiveUsers: 0
    });
    const [connected, setConnected] = useState(false);

    useEffect(() => {
        // Fetch initial data
        fetchUserCount();

        // Setup WebSocket connection
        const socket = new SockJS('http://localhost:8080/ws');
        const stompClient = new Client({
            webSocketFactory: () => socket,
            debug: (str) => {
                console.log(str);
            },
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        stompClient.onConnect = () => {
            console.log('Connected to WebSocket');
            setConnected(true);

            // Subscribe to user count updates
            stompClient.subscribe('/topic/dashboard/users', (message) => {
                const data = JSON.parse(message.body);
                setUserCount(data);
                console.log('Received update:', data);
            });
        };

        stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
            setConnected(false);
        };

        stompClient.activate();

        // Cleanup on unmount
        return () => {
            if (stompClient.active) {
                stompClient.deactivate();
            }
        };
    }, []);

    const fetchUserCount = async () => {
        try {
            const response = await fetch('http://localhost:8080/api/v1/admin/dashboard/users/count');
            const data = await response.json();
            setUserCount(data);
        } catch (error) {
            console.error('Error fetching user count:', error);
        }
    };

    return (
        <div className="dashboard">
            <h1>Dashboard - Real-time User Statistics</h1>

            <div className="connection-status">
                <p>Status: {connected ? '🟢 Connected' : '🔴 Disconnected'}</p>
            </div>

            <div className="stats-container">
                <div className="stat-card">
                    <h3>Total Users</h3>
                    <p className="stat-value">{userCount.totalUsers}</p>
                </div>

                <div className="stat-card">
                    <h3>Active Users</h3>
                    <p className="stat-value">{userCount.activeUsers}</p>
                </div>

                <div className="stat-card">
                    <h3>Inactive Users</h3>
                    <p className="stat-value">{userCount.inactiveUsers}</p>
                </div>
            </div>

            <style jsx>{`
                .dashboard {
                    padding: 20px;
                    max-width: 1200px;
                    margin: 0 auto;
                }

                .connection-status {
                    margin-bottom: 20px;
                    padding: 10px;
                    background: #f5f5f5;
                    border-radius: 8px;
                }

                .stats-container {
                    display: grid;
                    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                    gap: 20px;
                }

                .stat-card {
                    background: white;
                    padding: 30px;
                    border-radius: 8px;
                    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                    text-align: center;
                }

                .stat-card h3 {
                    margin: 0 0 15px 0;
                    color: #333;
                    font-size: 18px;
                }

                .stat-value {
                    font-size: 48px;
                    font-weight: bold;
                    color: #007bff;
                    margin: 0;
                }
            `}</style>
        </div>
    );
};

export default Dashboard;
