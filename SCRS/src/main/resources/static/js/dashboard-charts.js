// Dashboard Charts using Chart.js
document.addEventListener('DOMContentLoaded', function() {
    // Admin Dashboard Chart - Mixed Chart with Financial and User Data
    if (document.getElementById('adminChart')) {
        const canvas = document.getElementById('adminChart');
        const pendingApprovals = parseInt(canvas.getAttribute('data-pending-approvals')) || 0;
        const superUsers = parseInt(canvas.getAttribute('data-super-users')) || 0;
        const totalRevenue = parseFloat(canvas.getAttribute('data-total-revenue').replace(/[^0-9.-]+/g,"")) || 0;
        const unpaidInvoices = parseInt(canvas.getAttribute('data-unpaid-invoices')) || 0;

        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Pending Approvals', 'Super Users', 'Unpaid Invoices'],
                datasets: [{
                    label: 'Count',
                    data: [pendingApprovals, superUsers, unpaidInvoices],
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.8)',
                        'rgba(54, 162, 235, 0.8)',
                        'rgba(255, 205, 86, 0.8)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 205, 86, 1)'
                    ],
                    borderWidth: 2,
                    borderRadius: 8,
                    borderSkipped: false,
                    hoverBackgroundColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 205, 86, 1)'
                    ],
                    hoverBorderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    title: {
                        display: true,
                        text: 'Administrative Overview',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ' + context.parsed.y;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        },
                        ticks: {
                            stepSize: 1
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                animation: {
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });

        // Add a separate revenue display
        const revenueDisplay = document.createElement('div');
        revenueDisplay.className = 'revenue-card';
        revenueDisplay.style.background = 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)';
        revenueDisplay.innerHTML = `
            <h3>Total Revenue</h3>
            <p>₹${totalRevenue.toLocaleString()}</p>
        `;
        canvas.parentNode.appendChild(revenueDisplay);
    }

    // Director Dashboard Chart - Enhanced Department Statistics
    if (document.getElementById('directorChart')) {
        const canvas = document.getElementById('directorChart');
        const courseCount = parseInt(canvas.getAttribute('data-course-count')) || 0;
        const studentCount = parseInt(canvas.getAttribute('data-student-count')) || 0;
        const facultyCount = parseInt(canvas.getAttribute('data-faculty-count')) || 0;
        const assignmentCount = parseInt(canvas.getAttribute('data-assignment-count')) || 0;

        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: ['Courses', 'Students', 'Faculty', 'Assignments'],
                datasets: [{
                    label: 'Department Statistics',
                    data: [courseCount, studentCount, facultyCount, assignmentCount],
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.9)',
                        'rgba(54, 162, 235, 0.9)',
                        'rgba(255, 205, 86, 0.9)',
                        'rgba(75, 192, 192, 0.9)'
                    ],
                    borderColor: [
                        'rgba(255, 99, 132, 1)',
                        'rgba(54, 162, 235, 1)',
                        'rgba(255, 205, 86, 1)',
                        'rgba(75, 192, 192, 1)'
                    ],
                    borderWidth: 3,
                    hoverBorderWidth: 5,
                    hoverBorderColor: '#fff',
                    hoverOffset: 15,
                    shadowOffsetX: 2,
                    shadowOffsetY: 2,
                    shadowBlur: 8,
                    shadowColor: 'rgba(0, 0, 0, 0.15)'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            padding: 20,
                            usePointStyle: true,
                            font: {
                                size: 12
                            }
                        }
                    },
                    title: {
                        display: true,
                        text: 'Department Composition',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                const total = context.dataset.data.reduce((a, b) => a + b, 0);
                                const percentage = ((context.parsed / total) * 100).toFixed(1);
                                return context.label + ': ' + context.parsed + ' (' + percentage + '%)';
                            }
                        }
                    }
                },
                animation: {
                    animateScale: true,
                    animateRotate: true,
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });
    }

    // Staff Dashboard Chart - Enhanced Financial Analytics
    if (document.getElementById('staffChart')) {
        const canvas = document.getElementById('staffChart');
        const feeCount = parseInt(canvas.getAttribute('data-fee-count')) || 0;
        const totalRevenue = parseFloat(canvas.getAttribute('data-total-revenue').replace(/[^0-9.-]+/g,"")) || 0;
        const unpaidCount = parseInt(canvas.getAttribute('data-unpaid-count')) || 0;

        const ctx = canvas.getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: {
                labels: ['Fee Structures', 'Unpaid Invoices'],
                datasets: [{
                    label: 'Count',
                    data: [feeCount, unpaidCount],
                    backgroundColor: [
                        'rgba(75, 192, 192, 0.8)',
                        'rgba(255, 99, 132, 0.8)'
                    ],
                    borderColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)'
                    ],
                    borderWidth: 2,
                    borderRadius: 8,
                    borderSkipped: false,
                    hoverBackgroundColor: [
                        'rgba(75, 192, 192, 1)',
                        'rgba(255, 99, 132, 1)'
                    ],
                    hoverBorderWidth: 3
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    },
                    title: {
                        display: true,
                        text: 'Financial Operations Overview',
                        font: {
                            size: 16,
                            weight: 'bold'
                        }
                    },
                    tooltip: {
                        callbacks: {
                            label: function(context) {
                                return context.label + ': ' + context.parsed.y;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.1)'
                        },
                        ticks: {
                            stepSize: 1
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                },
                animation: {
                    duration: 2000,
                    easing: 'easeInOutQuart'
                }
            }
        });

        // Add revenue display for staff
        const revenueDisplay = document.createElement('div');
        revenueDisplay.className = 'revenue-card';
        revenueDisplay.style.background = 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)';
        revenueDisplay.innerHTML = `
            <h3>Total Revenue Generated</h3>
            <p>₹${totalRevenue.toLocaleString()}</p>
        `;
        canvas.parentNode.appendChild(revenueDisplay);
    }
});